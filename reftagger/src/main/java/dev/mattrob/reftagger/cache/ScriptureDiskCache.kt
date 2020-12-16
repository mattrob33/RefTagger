package dev.mattrob.reftagger.cache

import android.content.Context

internal class ScriptureDiskCache private constructor(
	private val appContext: Context
): ScriptureCache {

	companion object {
		private const val DISK_CACHE_PREFS = "reftagger_cache"

		private var instance: ScriptureDiskCache? = null

		fun getInstance(appContext: Context): ScriptureDiskCache {
			if (instance == null)
				instance = ScriptureDiskCache(appContext)
			return instance!!
		}
	}

	private val prefs = appContext.getSharedPreferences(DISK_CACHE_PREFS, Context.MODE_PRIVATE)

	override fun getText(reference: String, version: String): String? {
		synchronized(this) {
			return prefs.getString(getKey(reference, version), null)
		}
	}

	override fun putText(reference: String, version: String, text: String) {
		synchronized(this) {
			val editor = prefs.edit()
			editor.putString(getKey(reference, version), text)
			editor.apply()
		}
	}

	private fun getKey(reference: String, version: String) = "${version}_${reference}"
}