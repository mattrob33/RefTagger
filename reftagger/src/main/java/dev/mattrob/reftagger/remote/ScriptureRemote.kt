package dev.mattrob.reftagger.remote

import dev.mattrob.reftagger.data.Response
import dev.mattrob.reftagger.usecases.GetBibleGatewayApiUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class ScriptureRemote(
	private val getBibleGatewayApi: GetBibleGatewayApiUseCase
) {

	private val scope = GlobalScope

	fun getScripture(reference: String,
					 version: String,
					 onResult: (Response) -> Unit)
	{
		scope.launch(Dispatchers.IO) {
			val path = getBibleGatewayApi(reference, version)
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
					onResult(Response.Success(text))
				}
			}
			else {
				scope.launch(Dispatchers.Main) {
					onResult(
						Response.Error(
							message = "Unable to connect. Server returned response ${conn.responseCode}"
						)
					)
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
}