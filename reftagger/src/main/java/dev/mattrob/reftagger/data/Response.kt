package dev.mattrob.reftagger.data

sealed class Response {
	data class Success(
		val text: String
	): Response()

	data class Error(
		val message: String
	): Response()
}