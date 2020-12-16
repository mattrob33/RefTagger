package dev.example.mattrob.reftagger

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.mattrobertson.reftagger.R
import dev.mattrob.reftagger.RefTagger

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val tvMain = findViewById<TextView>(R.id.tvMain)
		tvMain.movementMethod = LinkMovementMethod()

		val refTagger = RefTagger.Builder()
			.enableDiskCache(applicationContext)
			.italicizeLinks(true)
			.setLinkColor(Color.DKGRAY)
			.build()

		val editor = findViewById<EditText>(R.id.editor)
		editor.doAfterTextChanged { text ->
			tvMain.text = refTagger.tag(text.toString())
		}
	}
}