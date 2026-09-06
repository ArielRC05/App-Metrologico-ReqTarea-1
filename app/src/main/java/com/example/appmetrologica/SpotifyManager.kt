package com.example.appmetrologica

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class SpotifyManager(private val context: Context) {

    fun reproducirPlaylist(playlistUriInput: String, onError: (String) -> Unit) {
        try {
            val uriFinal = formatearUriSpotify(playlistUriInput)

            // Intent que solicita abrir la app de spotify
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriFinal)).apply {
                setPackage("com.spotify.music")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            onError("La aplicación de Spotify no está instalada en el dispositivo.")
        } catch (e: Exception) {
            onError("Error al abrir Spotify: ${e.localizedMessage}")
        }
    }

    fun desconectar() {
        // Uso de Intent debido a errores de la SDK de Spotify
    }

    private fun formatearUriSpotify(input: String): String {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("spotify:playlist:") -> trimmed
            trimmed.contains("open.spotify.com/playlist/") -> {
                val id = trimmed.substringAfter("playlist/").substringBefore("?")
                "spotify:playlist:$id"
            }
            else -> "spotify:playlist:$trimmed"
        }
    }
}