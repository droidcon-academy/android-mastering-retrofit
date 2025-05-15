package com.droidcon.droidflix.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.droidflix.data.FlixApi
import com.droidcon.droidflix.data.api
import com.droidcon.droidflix.data.model.Auth
import com.droidcon.droidflix.data.model.Flix
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import java.io.IOException
import javax.inject.Inject
import androidx.core.net.toUri
import com.droidcon.droidflix.data.auth.TokenProvider
import com.droidcon.droidflix.data.model.FlixErrorResponse
import com.droidcon.droidflix.data.model.parseErrorBody
import com.droidcon.droidflix.data.prefs.AppPreferences
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.source
import retrofit2.Converter
import java.io.OutputStream

@HiltViewModel
class FlixViewModel @Inject constructor(
    private val hiltApi: FlixApi,
    private val errorConverter: Converter<ResponseBody, FlixErrorResponse>
) : ViewModel() {

    private val _flix = MutableStateFlow<Flix?>(null)
    val flix: StateFlow<Flix?> = _flix
    var uiState by mutableStateOf<FlixUiState>(FlixUiState.Idle)
        private set
    val auth = mutableStateOf(Auth())

    fun getFlix(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            val response = hiltApi.getFlix(id)
            _flix.emit(response.body())
            uiState = when {
                !response.isSuccessful -> FlixUiState.ApiError(response.parseErrorBody(errorConverter))
                else -> FlixUiState.Success
            }
        }
    }

    fun updateFlix(oldflix: Flix, newFlix: Flix, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            try {
                newFlix.poster?.let {
                    if (it != oldflix.poster) uploadFlixFile(it, contentResolver)
                }
                newFlix.video?.let {
                    if (it != oldflix.video) uploadFlixFile(it, contentResolver)
                }
                val response = hiltApi.editFlix(
                    oldflix.id,
                    newFlix.title,
                    newFlix.year,
                    newFlix.plot,
                    newFlix.poster ?: oldflix.poster ?: "",
                    newFlix.video ?: oldflix.video ?: ""
                )
                uiState = when {
                    !response.isSuccessful -> FlixUiState.ApiError(response.parseErrorBody(errorConverter))
                    else -> FlixUiState.Success
                }
            } catch (e: Exception) {
                uiState = FlixUiState.Error("Failed to edit flix: ${e.message}")
            }
        }
    }

    fun createFlix(flix: Flix, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            try {
                if (
                    !flix.poster.isNullOrBlank() &&
                    !uploadFlixFile(flix.poster, contentResolver)
                ) {
                    return@launch
                }
                if (
                    !flix.video.isNullOrBlank() &&
                    !uploadFlixFile(flix.video, contentResolver)
                ) {
                    return@launch
                }
                val response = hiltApi.addFlix(flix)
                uiState = when {
                    !response.isSuccessful -> FlixUiState.ApiError(response.parseErrorBody(errorConverter))
                    else -> FlixUiState.Success
                }
            } catch (e: Exception) {
                uiState = FlixUiState.Error("Failed to create flix: ${e.message}")
            }
        }
    }

    fun downloadFile(
        fileUrl: String,
        outputStream: OutputStream
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = api.downloadFile(fileUrl)

            if (!response.isSuccessful) {
                throw IOException("Failed to download: ${response.code()}")
            }

            val inputStream = response.body()?.byteStream()
                ?: throw IOException("Empty response body")

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private suspend fun uploadFlixFile(path: String, contentResolver: ContentResolver): Boolean {
        val uri = path.toUri()
        val filePart = uriToMultipart(contentResolver, uri)

        val response = api.uploadFile(
            filePart = filePart,
            token = TokenProvider().getAccessToken()
        )

        return when {
            !response.isSuccessful || response.body()?.status != "SUCCESS" -> {
                FlixUiState.ApiError(response.parseErrorBody(errorConverter))
                false
            }
            else -> true
        }
    }

    private fun uriToMultipart(
        contentResolver: ContentResolver,
        uri: Uri,
        partName: String = "file",
        fileName: String = "upload_file"
    ): MultipartBody.Part {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IOException("Cannot open input stream from URI: $uri")

        val requestBody = object : RequestBody() {
            override fun contentType() = (contentResolver.getType(uri) ?: "application/octet-stream").toMediaType()

            override fun writeTo(sink: BufferedSink) {
                inputStream.source().use { source ->
                    sink.writeAll(source)
                }
            }
        }

        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }

    fun saveCredentials() {
        auth.value.let {
            AppPreferences.username = it.username
            AppPreferences.password = it.password
            AppPreferences.timestamp = System.currentTimeMillis()
        }
    }

    fun clearState() {
        uiState = FlixUiState.Idle
    }
}