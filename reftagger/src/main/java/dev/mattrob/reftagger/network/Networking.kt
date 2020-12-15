package dev.mattrob.reftagger.network

import dev.mattrob.reftagger.handler.ClickHandler
import dev.mattrob.reftagger.utils.getBibleGatewayApiUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun fetchScriptureText(ref: String, version: String, clickHandler: ClickHandler.ScriptureText) {

    val scope = GlobalScope

    scope.launch(Dispatchers.IO) {
        val path = getBibleGatewayApiUrl(ref, version)
        val url = URL(path)

        val conn = url.openConnection() as HttpsURLConnection
        conn.apply {
            connectTimeout = 30000
            readTimeout = 30000
            requestMethod = "GET"
        }
        conn.connect()

        val isOK = (conn.responseCode == HttpsURLConnection.HTTP_OK)
        if (isOK) {
            val reader = conn.inputStream.bufferedReader()
            val response = reader.readText()
            reader.close()

            val text = getTextFromBibleGatewayApiResponse(response)

            scope.launch(Dispatchers.Main) {
                clickHandler.onSuccess(ref, text)
            }
        }
        else {
            scope.launch(Dispatchers.Main) {
                clickHandler.onError("Unable to connect. Server returned response ${conn.responseCode}")
            }
        }
    }
}

/**
 * Strips unnecessary javascript callback & json data from Bible Gateway API response.
 */
private fun getTextFromBibleGatewayApiResponse(response: String): String {
    var text = response.substringAfter("\"text\" : \"")
    text = text.substringBefore("\", \"version\"")
    return text
}