package dev.mattrob.reftagger.cache

internal class ScriptureMemCache private constructor(): ScriptureCache {

	companion object {
		private var instance: ScriptureMemCache? = null

		fun getInstance(): ScriptureMemCache {
			if (instance == null)
				instance = ScriptureMemCache()
			return instance!!
		}
	}

	private val cache = HashMap<String, HashMap<String, String>>()

	override fun getText(reference: String, version: String): String? {
		synchronized(this) {
			return cache[version]?.get(reference)
		}
	}

	override fun putText(reference: String, version: String, text: String) {
		synchronized(this) {
			if (cache[version] == null)
				cache[version] = HashMap()
			cache[version]!![reference] = text
		}
	}
}