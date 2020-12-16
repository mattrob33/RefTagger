package dev.mattrob.reftagger.usecases

internal class GetBibleGatewayApiUseCase {
	/**
	 * Generate the Bible Gateway *API* URL for [reference]. This URL ***should not*** be provided to a web browser and should only be used for API calls.
	 */
	operator fun invoke(reference: String, version: String) = "https://www.biblegateway.com/share/tooltips/data/?search=$reference&version=$version&callback=callback"
}