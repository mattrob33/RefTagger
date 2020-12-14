package com.mattrobertson.reftagger.handler

sealed class ClickHandler {
    abstract class BibleGatewayURL: ClickHandler() {
        abstract fun onClick(url: String)
    }

    abstract class ScriptureReference: ClickHandler() {
        abstract fun onClick(reference: String)
    }

    abstract class ScriptureText: ClickHandler() {
        abstract fun onSuccess(text: String)
        abstract fun onError(message: String)
    }
}