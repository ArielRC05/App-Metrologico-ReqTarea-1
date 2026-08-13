package com.example.appmetrologica

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.appmetrologica.ui.theme.AppMetrologicaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    //val es variable solo lectura y var es mutable
    //lateinit iniciar tarde para evitar null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this) //this es el contexto actual osea en este caso mainactivit

        enableEdgeToEdge()
        setContent {
            var latitude by remember { mutableStateOf<Double?>(null) } //? es preguntar si puede ser double o null
            var longitude by remember { mutableStateOf<Double?>(null) }
            AppMetrologicaTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp), //modifier es como se comporta el componente (ubicacion tamano color etc)
                    verticalArrangement = Arrangement.SpaceEvenly,         //dp significa density independent pixels osea los pixeles adaptados
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            getLocation(receiveLocation = { getLatitude, getLongitude ->
                                latitude = getLatitude
                                longitude = getLongitude
                            })
                        }
                    ) {
                        Text("Registrar Lecturaaa")
                    }
                    Box(
                        modifier = Modifier.size(250.dp).background(Color.LightGray,
                            RoundedCornerShape(20.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ){
                        Column(
                        ){
                            Text("Latitud: ${latitude ?: "Sin registrar"}")//?: en caso de ser null poner eso
                            Spacer(modifier = Modifier.height(30.dp))
                            Text("Longitud: ${longitude ?: "Sin registrar"}")
                        }

                    }
                }

            }
        }

    }
    //fun = function
    private fun getLocation(receiveLocation: (Double, Double) -> Unit) {
        if ( //preguntar si tenemos los permisos
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) { //en caso contrario pedir permisos
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
                return
            }
        //conseguimos la ubicacion actual
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, //alta presicion
            null //Cancellation token: para cancelar solicitud (con boton no es necesario creo)
        )
            .addOnSuccessListener { location -> //addOnSuccessListener es esperar hasta tener la ubicacion
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    receiveLocation(latitude, longitude)

                } else {
                    println("No se pudo lograr conseguir la ubicacion")
                }
            }
    }
}
