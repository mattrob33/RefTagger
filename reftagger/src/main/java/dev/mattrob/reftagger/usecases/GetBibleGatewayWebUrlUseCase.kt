package dev.mattrob.reftagger.usecases

internal class GetBibleGatewayWebUrlUseCase {
	/**
	 * Generate the Bible Gateway URL for [reference]. This is the URL that should be provided to a web browser.
	 */
	operator fun invoke(reference: String, version: String) = "https://www.biblegateway.com/passage/?search=$reference&version=$version"
}