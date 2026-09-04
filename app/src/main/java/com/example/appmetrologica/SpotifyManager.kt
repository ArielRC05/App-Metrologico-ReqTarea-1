package com.example.appmetrologica

import android.content.Context
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

class SpotifyManager(private val context: Context) {

    // conexion con el CLIENT ID que me da spotify dev dashboard
    private val clientId = "61f3281f9ace43ffb34523a734816244"
    private val redirectUri = "appmetrologica://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    fun reproducirPlaylist(playlistUriInput: String, onError: (String) -> Unit) {
        // formatea la entrada a formato URI nativo (spotify:playlist:ID)
        val uriFinal = formatearUriSpotify(playlistUriInput)

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                // Inicia la reproducción continua de la lista de reproducción
                spotifyAppRemote?.playerApi?.play(uriFinal)
            }

            override fun onFailure(throwable: Throwable) {
                onError(throwable.message ?: "Error al conectar con Spotify")
            }
        })
    }

    fun desconectar() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    private fun formatearUriSpotify(input: String): String {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("spotify:playlist:") -> trimmed
            trimmed.contains("open.spotify.com/playlist/") -> {
                val id = trimmed.substringAfter("playlist/").substringBefore("?")
                "spotify:playlist:$id"
            }
            else -> "spotify:playlist:$trimmed" // Intenta usar el string directo como ID
        }
    }
}