package com.example.appmetrologica

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable //funcion de composable
fun SpotifyUI(
    onRepdroducirClick: (String) -> Unit,
    mensajeEstado: String
) {
    var urlInput by remember {mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {Text( // ma texto
        text = "Repductor Spotify",
        style =  MaterialTheme.typography.headlineSmall
    )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField( // texto outlined
            value = urlInput,
            onValueChange = {urlInput = it},
            label = { Text("Enlace o URI de la Playlist")},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp)) //espacio

        Button( //boton
            onClick = {onRepdroducirClick(urlInput)}, // funcion de click
            enabled = urlInput.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir Playlist Completa") //texto boton
        }

        if (mensajeEstado.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mensajeEstado,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

}