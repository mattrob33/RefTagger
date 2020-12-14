package com.mattrobertson.reftagger.tagger

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mattrobertson.reftagger.handler.ClickHandler
import com.mattrobertson.reftagger.handler.ClickHandler.*
import com.mattrobertson.reftagger.network.fetchScriptureText
import com.mattrobertson.reftagger.utils.createBibleGatewayUrl

class RefTagger private constructor(builder: Builder) {

    private var clickHandler: ClickHandler? = builder.clickHandler

    private var handlerType = builder.handlerType

    private var defaultVersion = builder.defaultVersion

    companion object {
        private val REFERENCE_REGEX = "((?:(Genesis|Gen?|Gn|Exodus|Exod?|Ex|Leviticus|Le?v|Numbers|Nu?m|Nu|Deuteronomy|Deut?|Dt|Josh?ua|Josh?|Jsh|Judges|Ju?dg|Jg|Ru(?:th)?|Ru?t|(?:1|i|2|ii) ?Samuel|(?:1|i|2|ii) ?S(?:a|m)|(?:1|i|2|ii) ?Sam|(?:1|i|2|ii) ?Kin(?:gs?)?|(?:1|i|2|ii) ?Kgs|(?:1|i|2|ii) ?Chronicles|(?:1|i|2|ii) ?Chr(?:o?n)?|(?:1|i|2|ii) ?Cr|Ezra?|Nehemiah|Neh?|Esther|Esth?|Jo?b|Psalms?|Psa?|Proverbs|Pro?v?|Ecclesiastes|Ec(?:cl?)?|Song (?:O|o)f Solomon|Song (?:O|o)f Songs?|Son(?:gs?)?|SS|Isaiah?|Isa?|Jeremiah|Je?r|Lamentations|La(?:me?)?|Ezekiel|Eze?k?|Daniel|Da?n|Da|Hosea|Hos?|Hs|Jo(?:el?)?|Am(?:os?)?|Obadiah|Ob(?:ad?)?|Jon(?:ah?)?|Jnh|Mic(?:ah?)?|Mi|Nah?um|Nah?|Habakkuk|Hab|Zephaniah|Ze?ph?|Haggai|Hagg?|Hg|Zechariah|Ze?ch?|Malachi|Ma?l|Matthew|Matt?|Mt|Mark|Ma(?:r|k)|M(?:r|k)|Luke?|Lk|Lu?c|John|Jn|Ac(?:ts?)?|Romans|Ro?m|(?:1|i|2|ii) ?Corinthians|(?:1|i|2|ii) ?C(?:or?)?|Galatians|Gal?|Gl|Ephesians|Eph?|Philippians|Phil|Colossians|Co?l|(?:1|i|2|ii) ?Thessalonians|(?:1|i|2|ii) ?Th(?:e(?:ss?)?)?|(?:1|i|2|ii) ?Timothy|(?:1|i|2|ii) ?Tim|(?:1|i|2|ii) ?T(?:i|m)|Ti(?:tus)?|Ti?t|Philemon|Phl?m|Hebrews|Heb?|Jam(?:es)?|Jms|Jas|(?:1|i|2|ii) ?Peter|(?:1|i|2|ii) ?Pe?t?|(?:1|i|2|ii|3|iii) ?J(?:oh)?n?|Jude?|Revelations?|Rev|R(?:e|v))(?:.)? *?)?(?:(\\d*):)?(\\d+(?:(?:ff|f|\\w)|(?:\\s?(?:-|–|—)\\s?\\d+)?)))([^a-z0-9]*)".toRegex()
    }

    class Builder {
        internal var clickHandler: ClickHandler? = null
        internal var handlerType = HandlerType.IGNORE_CLICKS
        internal var defaultVersion = "NIV"

        fun useDialog(): Builder {
            handlerType = HandlerType.DIALOG
            return this
        }

        fun useExternalBrowser(): Builder {
            handlerType = HandlerType.WEB_BROWSER
            return this
        }

        fun setClickHandler(handler: ClickHandler): Builder {
            handlerType = HandlerType.CUSTOM
            clickHandler = handler
            return this
        }

        fun setDefaultVersion(version: String): Builder {
            defaultVersion = version
            return this
        }

        fun build() = RefTagger(this)
    }

    internal enum class HandlerType {
        WEB_BROWSER,
        DIALOG,
        CUSTOM,
        IGNORE_CLICKS
    }

    fun autoTag(text: String): Spanned {
        val sb = SpannableStringBuilder("")

        val matches = REFERENCE_REGEX.findAll(text).toList()

        var iChar = 0
        var iMatch = 0

        while (iChar < text.length) {
            if (iMatch < matches.size) {
                val groups = matches[iMatch].groups
                if (groups.size > 1) {
                    val match = groups[1]!!
                    val from = match.range.first
                    val to = match.range.last

                    if (iChar < from) {
                        sb.append(text.subSequence(iChar, from))
                        iChar = from
                    }
                    else {
                        sb.append(createLinkSpan(match))
                        iChar = to + 1
                        iMatch += 1
                    }
                }
            }
            else {
                sb.append(text.subSequence(iChar, text.length))
                iChar = text.length
            }
        }

        return sb
    }

    private fun createLinkSpan(match: MatchGroup): SpannableString {
        val ref = match.value
        val from = match.range.first
        val to = match.range.last

        val link = object: ClickableSpan() {
            override fun onClick(view: View) {
                onClick(ref, view)
            }
        }

        return SpannableString(ref).apply {
            setSpan(link, 0, to - from + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun onClick(ref: String, view: View) {
        when (handlerType) {
            HandlerType.WEB_BROWSER -> {
                val url = createBibleGatewayUrl(ref, defaultVersion)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                view.context.startActivity(intent)
            }
            HandlerType.DIALOG -> {
                val handler = object: ScriptureText() {
                    override fun onSuccess(text: String) {
                        val builder = AlertDialog.Builder(view.context)
                        val dialog = builder.setTitle(ref)
                            .setMessage(text)
                            .create()
                        dialog.show()
                    }

                    override fun onError(message: String) {
                        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
                    }
                }

                fetchScriptureText(ref, defaultVersion, handler)
            }
            HandlerType.CUSTOM -> {
                when (val clickHandler = clickHandler) {
                    is BibleGatewayURL -> clickHandler.onClick(createBibleGatewayUrl(ref, defaultVersion))
                    is ScriptureReference -> clickHandler.onClick(ref)
                    is ScriptureText -> fetchScriptureText(ref, defaultVersion, clickHandler)
                }
            }
            HandlerType.IGNORE_CLICKS -> {}
        }
    }
}