package dev.mattrob.reftagger.repo

import android.content.Context
import dev.mattrob.reftagger.cache.ScriptureDiskCache
import dev.mattrob.reftagger.cache.ScriptureMemCache
import dev.mattrob.reftagger.data.Response
import dev.mattrob.reftagger.remote.ScriptureRemote
import dev.mattrob.reftagger.usecases.GetBibleGatewayApiUseCase

internal class ScriptureRepo(
	getBibleGatewayApi: GetBibleGatewayApiUseCase,
	appContext: Context? = null
) {

	private val memCache = ScriptureMemCache.getInstance()

	private val diskCache: ScriptureDiskCache? = if (appContext != null)
		ScriptureDiskCache.getInstance(appContext)
	else
		null

	private val remote = ScriptureRemote(getBibleGatewayApi)

	fun getScripture(reference: String,
					 version: String,
					 onResult: (Response) -> Unit)
	{
		val memCachedText = memCache.getText(reference, version)
		if (memCachedText != null) {
			onResult(Response.Success(memCachedText))
			return
		}

		diskCache?.let { diskCache ->
			val diskCachedText = diskCache.getText(reference, version)
			if (diskCachedText != null) {
				memCache.putText(reference, version, diskCachedText)
				onResult(Response.Success(diskCachedText))
				return
			}
		}

		remote.getScripture(reference, version) { response ->
			when (response) {
				is Response.Success -> {
					memCache.putText(reference, version, response.text)
					diskCache?.putText(reference, version, response.text)

					onResult(Response.Success(response.text))
				}
				is Response.Error -> {
					onResult(Response.Error(response.message))
				}
			}
		}
	}

}