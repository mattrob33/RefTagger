package dev.mattrob.reftagger.cache

interface ScriptureCache {

	fun getText(reference: String, version: String): String?

	fun putText(reference: String, version: String, text: String)

}