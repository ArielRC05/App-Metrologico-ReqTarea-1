// jose marin
// kendall ariel

package com.example.appmetrologica

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
//imports para la caja de producto (ya la hice icono pero por si acaso tambien pondre el png por aca)
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@Composable
fun LecturasInterfaz(
    onObtenerUbicacion: (onLocationReceived: (Double, Double) -> Unit) -> Unit,
    viewModel: LecturaViewModel = viewModel()
) {
    // VARIABLES DE LOS DATOS
    val listaLecturas by viewModel.lecturas.collectAsState(initial = emptyList())

    var temperaturaText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var cargandoUbicacion by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image( //la caja del producto
            painter = painterResource(id = R.drawable.caja_producto), //png en res drawable
            contentDescription = "Caja del producto",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "Registro Metrológico",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // INPUT TEMP
        OutlinedTextField(
            value = temperaturaText,
            onValueChange = { input ->
                temperaturaText = input
                isError = input.isNotEmpty() && input.toDoubleOrNull() == null
            },
            label = { Text("Temperatura (°C)") },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = {
                if (isError) Text("Ingrese un número válido")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BOTON
        Button(
            onClick = {
                val tempValue = temperaturaText.toDoubleOrNull()
                if (tempValue != null) {
                    cargandoUbicacion = true
                    // Obtiene las coordenadas GPS actuales
                    onObtenerUbicacion { latitud, longitud ->
                        cargandoUbicacion = false

                        // Guardar en la Base de Datos Room
                        viewModel.agregarLectura(
                            temperatura = tempValue,
                            latitud = latitud,
                            longitud = longitud
                        )

                        // Limpiar el campo de texto tras guardar
                        temperaturaText = ""
                    }
                } else {
                    isError = true
                }
            },
            enabled = temperaturaText.isNotEmpty() && !isError && !cargandoUbicacion,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargandoUbicacion) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Guardar Lectura")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Historial de Lecturas:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // LISTA
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listaLecturas) { lectura ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        //para la fecha y hora
                        val fechaFormateada = remember(lectura.fechaHora) {
                            SimpleDateFormat("dd/MM/yyy HH:mm:ss", Locale.getDefault()).format(Date(lectura.fechaHora))
                        }

                        Text("Fecha: $fechaFormateada")
                        Text("Temperatura: ${lectura.temperatura} °C")
                        Text("Latitud: ${lectura.latitud}")
                        Text("Longitud: ${lectura.longitud}")
                    }
                }
            }
        }
    }
}