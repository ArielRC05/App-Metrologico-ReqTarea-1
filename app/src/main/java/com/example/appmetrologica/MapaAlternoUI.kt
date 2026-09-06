package com.example.appmetrologica

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
//imports de osmdroid
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource //proveedor
import org.osmdroid.util.GeoPoint //coordenada latitud y longitud
import org.osmdroid.views.MapView //al igual que con google el mapa como tal
import org.osmdroid.views.overlay.Marker

//Al hacer pruebas OSM me bloqueo Access Blocked 403 app is not.. puesto que ya no dejo, entonces como altenativa CartoDB
//provedor de los "tiles" del mapa
val cartoDBTiles = XYTileSource(
    "CartoDB",
    1, 18, 256, ".png",
    arrayOf( //servidores de los mapas
        "https://cartodb-basemaps-a.global.ssl.fastly.net/light_all/",
        "https://cartodb-basemaps-b.global.ssl.fastly.net/light_all/",
        "https://cartodb-basemaps-c.global.ssl.fastly.net/light_all/"
    )
)

@Composable
fun MapaAlternoUI(latitud: Double?,longitud: Double?) {
    val context = LocalContext.current //android seria el contexto

    remember { //config para osmdroid
        val prefs = context.getSharedPreferences("osmdroid_prefs", android.content.Context.MODE_PRIVATE)
        val config = Configuration.getInstance()
        config.load(context, prefs)
        config.userAgentValue = "AppMetrologicaAndroidApp/1.0"
        true
    }

    if (latitud != null && longitud != null) {
        val puntoUbicacion = remember(latitud, longitud) {
            GeoPoint(latitud, longitud) //coordenada latitud y longitud
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(cartoDBTiles) //se usa cartoDB por los bloqueos que daba Open Stree Map
                    setMultiTouchControls(true) //permite controlar le mapa con los dedos
                    controller.setZoom(18.0)
                    controller.setCenter(puntoUbicacion)

                    val marker = Marker(this).apply { //el pin de la ubicacion
                        position = puntoUbicacion
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    overlays.add(marker)
                }
            },
            update = { mapView -> //para actualizar la vista que crea factory, el mapa basicamente
                mapView.controller.setCenter(puntoUbicacion)
                mapView.overlays.filterIsInstance<Marker>().forEach { marker ->
                    marker.position = puntoUbicacion
                }
                mapView.invalidate()
            }
        )

        DisposableEffect(Unit) {
            onDispose { }
        }
    }
    else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() //cargando circular
        }
    }
}