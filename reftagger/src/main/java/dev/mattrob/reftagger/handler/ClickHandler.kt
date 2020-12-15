package dev.mattrob.reftagger.handler

sealed class ClickHandler {
    abstract class BibleGatewayURL: ClickHandler() {
        abstract fun onClick(url: String)
    }

    abstract class ScriptureReference: ClickHandler() {
        abstract fun onClick(reference: String)
    }

    abstract class ScriptureText: ClickHandler() {
        abstract fun onSuccess(ref: String, text: String)
        abstract fun onError(message: String)
    }
}