package com.mattrobertson.reftagger.utils

/**
 * Generate the Bible Gateway URL for [reference]. This is the URL that should be provided to a web browser.
 */
internal fun createBibleGatewayUrl(reference: String, version: String) = "https://www.biblegateway.com/passage/?search=$reference&version=$version"

/**
 * Generate the Bible Gateway *API* URL for [reference]. This URL ***should not*** be provided to a web browser and should only be used for API calls.
 */
internal fun getBibleGatewayApiUrl(reference: String, version: String) = "https://www.biblegateway.com/share/tooltips/data/?search=$reference&version=$version&callback=callback"