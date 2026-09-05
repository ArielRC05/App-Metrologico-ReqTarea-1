// jose marin
// kendall ariel

package com.example.appmetrologica

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.appmetrologica.ui.theme.AppMetrologicaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    //val es variable solo lectura y var es mutable
    //lateinit inicializar tarde
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mensajeEstadoSpotify by mutableStateOf("")

    private lateinit var SpotifyManager: SpotifyManager

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this) //this es el contexto actual osea en este caso mainactivit

        SpotifyManager = SpotifyManager(this) // Inicializa manager

        enableEdgeToEdge()
        setContent {
            AppMetrologicaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    var opcionSeleccionada by remember { mutableStateOf("registro") }

                    //Para el google maps CAMBIO
                    var latitud by remember { mutableStateOf<Double?>(null) }
                    var longitud by remember { mutableStateOf<Double?>(null) }

                    // MENU HAMBURGUESA ACA
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Text(
                                    text = "App Logistica",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                                HorizontalDivider()
                                // OPCIOn 1
                                NavigationDrawerItem(
                                    label = { Text("Registro Metrológico") },
                                    selected = opcionSeleccionada == "registro",
                                    onClick = {
                                        opcionSeleccionada = "registro"
                                        scope.launch { drawerState.close() }
                                    }
                                )
                                // OPCION 2
                                NavigationDrawerItem(
                                    label = { Text("Ubicación en Mapa") },
                                    selected = opcionSeleccionada == "mapa",
                                    onClick = {
                                        opcionSeleccionada = "mapa"
                                        scope.launch { drawerState.close() }
                                    }
                                )
                                //OPCION 3
                                NavigationDrawerItem(
                                    label = { Text("Reproductor Spotify") },
                                    selected = opcionSeleccionada == "spotify",
                                    onClick = {
                                        opcionSeleccionada = "spotify"
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            when (opcionSeleccionada) {// dependiendo de lo seleccionado, cambia de pantalla
                                                "mapa" -> "Ubicación en Mapa"
                                                "spotify" -> "Reproductor Spotify"
                                                else -> "Registro Metrológico" // para evitar pantallas en blanco vacias, default es reg met
                                            }
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menú") // nom men
                                        }
                                    }
                                )
                            }
                        ) { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                when (opcionSeleccionada) {
                                    "registro" -> {
                                        LecturasInterfaz( // loc
                                            onObtenerUbicacion = { receiveLocation ->
                                                getLocation(receiveLocation)
                                            }
                                        )
                                    }
                                    "mapa" -> {
                                        //conseguimos la ubicacion (reutilizando lo de registro metrologico)
                                        getLocation { latitude, longitude ->
                                            latitud = latitude
                                            longitud = longitude
                                        }
                                        println(latitud)
                                        println(longitud)
                                        //nos metemos a la interfaz para ver el mapa
                                        GoogleMapsUI( latitud, longitud)
                                    }
                                    "spotify" -> {
                                        SpotifyUI(
                                            onRepdroducirClick = { playlistInput ->
                                                mensajeEstadoSpotify = "" // limpiar errores prev
                                                SpotifyManager.reproducirPlaylist(
                                                    playlistUriInput = playlistInput,
                                                    onError = { error ->
                                                        mensajeEstadoSpotify = error
                                                    }
                                                )
                                            },
                                            mensajeEstado = mensajeEstadoSpotify
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SpotifyManager.desconectar() // Desconectar el servicio al cerrar
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

