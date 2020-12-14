package com.mattrobertson.reftagger.utils

fun createBibleGatewayUrl(ref: String, version: String) = "https://www.biblegateway.com/passage/?search=$ref&version=$version"

fun getBibleGatewayApiUrl(ref: String, version: String) = "https://www.biblegateway.com/share/tooltips/data/?search=$ref&version=$version&callback=callback"