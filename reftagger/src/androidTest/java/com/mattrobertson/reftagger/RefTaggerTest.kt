package com.mattrobertson.reftagger

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mattrobertson.reftagger.handler.RefWebBrowser
import com.mattrobertson.reftagger.tagger.RefTagger
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RefTaggerTest {

    lateinit var refTagger: RefTagger

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        refTagger = RefTagger.Builder().setClickHandler(RefWebBrowser(appContext)).build()
    }

    @Test
    fun refTagger_spanned_matches_original_text() {
        val text = "The references Gen 1:17 and Rom 5:12-21 are both in here."
        val spanned = refTagger.tag(text)
        assertEquals(text, spanned.toString())
    }
}