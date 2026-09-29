package com.example.myapplication.data

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import com.example.myapplication.data.models.PreferredLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Sarvam AI Text-To-Speech Manager handling API synthesis, audio playback, and native Android TTS fallback.
 */
object SarvamTtsManager {

    private const val API_URL = "https://api.sarvam.ai/text-to-speech"
    private const val API_KEY = "sk_vl3i9tms_3ts9pC5Few04dEm4vFkDFkFw"
    private const val MODEL_BULBUL = "bulbul:v3"
    private const val SPEAKER_SHUBH = "shubh"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .build()
    }

    private var mediaPlayer: MediaPlayer? = null
    private var nativeTts: TextToSpeech? = null
    private var currentTempFile: File? = null
    private var currentJob: Job? = null

    /**
     * Maps [PreferredLanguage] to its corresponding BCP-47 language tag required by Sarvam AI TTS API.
     */
    fun getBcp47Code(language: PreferredLanguage): String {
        return language.sarvamCode
    }

    /**
     * Synthesizes and plays text using Sarvam AI TTS API, falling back to Android's native TextToSpeech if network fails.
     */
    fun speak(
        context: Context,
        text: String,
        language: PreferredLanguage,
        scope: CoroutineScope,
        onStart: () -> Unit = {},
        onCompletion: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        stopSpeaking()

        onStart()

        currentJob = scope.launch(Dispatchers.IO) {
            val bcp47Code = getBcp47Code(language)
            var sarvamSuccess = false

            try {
                val jsonPayload = JSONObject().apply {
                    put("text", text)
                    put("language_code", bcp47Code)
                    put("model", MODEL_BULBUL)
                    put("speaker", SPEAKER_SHUBH)
                }.toString()

                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("api-subscription-key", API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)
                        val audios = jsonResponse.optJSONArray("audios")
                        if (audios != null && audios.length() > 0) {
                            val base64Audio = audios.getString(0)
                            val audioBytes = decodeBase64(base64Audio)
                            if (audioBytes != null && audioBytes.isNotEmpty()) {
                                val tempFile = File.createTempFile("sarvam_tts_", ".wav", context.cacheDir)
                                tempFile.writeBytes(audioBytes)
                                currentTempFile = tempFile

                                withContext(Dispatchers.Main) {
                                    playWavFile(
                                        file = tempFile,
                                        onCompletion = onCompletion,
                                        onError = {
                                            fallbackToNativeTts(context, text, language, onCompletion, onError)
                                        }
                                    )
                                }
                                sarvamSuccess = true
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                sarvamSuccess = false
            }

            if (!sarvamSuccess) {
                withContext(Dispatchers.Main) {
                    fallbackToNativeTts(context, text, language, onCompletion, onError)
                }
            }
        }
    }

    /**
     * Plays temporary audio file via Android MediaPlayer.
     */
    private fun playWavFile(
        file: File,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener {
                    stopSpeaking()
                    onCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    stopSpeaking()
                    onError()
                    true
                }
                prepare()
                start()
            }
            mediaPlayer = player
        } catch (_: Exception) {
            stopSpeaking()
            onError()
        }
    }

    /**
     * Fallback mechanism to play text using native Android TextToSpeech engine.
     */
    private fun fallbackToNativeTts(
        context: Context,
        text: String,
        language: PreferredLanguage,
        onCompletion: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val appContext = context.applicationContext
            var ttsInstance: TextToSpeech? = null
            ttsInstance = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val locale = getLocaleForLanguage(language)
                    val result = ttsInstance?.setLanguage(locale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        ttsInstance?.language = Locale("hi", "IN")
                    }

                    ttsInstance?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}

                        override fun onDone(utteranceId: String?) {
                            Handler(Looper.getMainLooper()).post {
                                stopSpeaking()
                                onCompletion()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            Handler(Looper.getMainLooper()).post {
                                stopSpeaking()
                                onError("Native TTS playback error")
                            }
                        }
                    })

                    val params = android.os.Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sarvam_fallback")
                    ttsInstance?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "sarvam_fallback")
                } else {
                    stopSpeaking()
                    onError("Native TTS initialization failed")
                }
            }
            nativeTts = ttsInstance
        } catch (e: Exception) {
            stopSpeaking()
            onError("TTS Fallback Failed: ${e.localizedMessage}")
        }
    }

    /**
     * Stops any current playback (MediaPlayer or native TTS), cleans up temporary files, and resets state.
     */
    fun stopSpeaking() {
        currentJob?.cancel()
        currentJob = null

        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null

        try {
            nativeTts?.let { tts ->
                tts.stop()
                tts.shutdown()
            }
        } catch (_: Exception) {}
        nativeTts = null

        try {
            currentTempFile?.let { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (_: Exception) {}
        currentTempFile = null
    }

    private fun decodeBase64(base64Str: String): ByteArray? {
        return try {
            Base64.decode(base64Str, Base64.DEFAULT)
        } catch (_: Exception) {
            try {
                java.util.Base64.getDecoder().decode(base64Str)
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun getLocaleForLanguage(language: PreferredLanguage): Locale {
        val parts = language.sarvamCode.split("-")
        val lang = parts.getOrNull(0) ?: "hi"
        val country = parts.getOrNull(1) ?: "IN"
        return Locale(lang, country)
    }
}
