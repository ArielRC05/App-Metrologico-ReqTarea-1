package com.example.appmetrologica

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition //posicion de la camara en el mapa
import com.google.android.gms.maps.model.LatLng //latitud longitud coordenada
import com.google.maps.android.compose.GoogleMap //el mapa como tal digamops
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState //evita recomposicion

@Composable
fun GoogleMapsUI(latitudGM: Double?, longitudGM: Double?) {
    if (latitudGM != null && longitudGM != null) {
        val ubicacion = LatLng(latitudGM,longitudGM) //coordenada
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(ubicacion,15f) //coloca la "camara" en la ubicacion y el zoom del mapa
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = ubicacion), //coloca el pin
            )
        }
    }
}