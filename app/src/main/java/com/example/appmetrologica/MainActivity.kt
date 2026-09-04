// jose marin
// kendall ariel

package com.example.appmetrologica

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.appmetrologica.ui.theme.AppMetrologicaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : ComponentActivity() {
    //val es variable solo lectura y var es mutable
    //lateinit inicializar tarde
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this) //this es el contexto actual osea en este caso mainactivit

        enableEdgeToEdge()
        setContent {
            AppMetrologicaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    LecturasInterfaz(
                        onObtenerUbicacion = { receiveLocation ->
                            getLocation(receiveLocation)
                        }
                    )
                }
            }
        }
    }
    private var pendingLocationCallback: ((Double, Double) -> Unit)? = null //lo que se pide cuando el usuario acepta
    private var locationPermissionLauncher = //comprobacion del permiso de ubicacion
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                pendingLocationCallback?.let { callback ->
                    getLocation(callback) //como tenemos los permisos ahora al entrar pasamos directo a conseguir la ubicacion
                }

                pendingLocationCallback = null

            } else {
                println("Permiso denegado")
                pendingLocationCallback = null
            }
        }
    //fun = function
    private fun getLocation(receiveLocation: (Double, Double) -> Unit) {



        if ( //preguntar si tenemos los permisos
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) { //en caso contrario pedir permisos
            pendingLocationCallback = receiveLocation //para cuando el usuario responda el pedido de permiso lo guardamos

            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        //conseguimos la ubicacion actual
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, //alta presicion
            null //Cancellation token: para cancelar solicitud (con boton no es necesario creo)
        )
            .addOnSuccessListener { location -> //addOnSuccessListener es esperar hasta tener la ubicacion (si termina bien)
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    receiveLocation(latitude, longitude)

                } else {
                    println("No se pudo lograr conseguir la ubicacion")
                }
            }
            .addOnFailureListener { exception -> //(si termina mal)
                println("Error consiguiendo la ubicacion: ${exception.message}")
            }
    }


}

