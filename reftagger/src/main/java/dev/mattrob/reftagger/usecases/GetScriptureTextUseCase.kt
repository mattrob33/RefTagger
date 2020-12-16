package dev.mattrob.reftagger.usecases

import android.content.Context
import dev.mattrob.reftagger.ClickHandler
import dev.mattrob.reftagger.data.Response
import dev.mattrob.reftagger.repo.ScriptureRepo

internal class GetScriptureTextUseCase(
	getBibleGatewayApi: GetBibleGatewayApiUseCase,
	appContext: Context? = null
) {

	private val repo = ScriptureRepo(getBibleGatewayApi, appContext)

	/**
	 * Fetches the biblical text for [reference] in [version] and calls [clickHandler] upon completion.
	 */
	operator fun invoke(reference: String,
						version: String,
						clickHandler: ClickHandler.ScriptureText)
	{
		repo.getScripture(reference, version) { response ->
			when (response) {
				is Response.Success -> clickHandler.onSuccess(reference, response.text)
				is Response.Error -> clickHandler.onError(response.message)
			}
		}
	}
}