// jose marin
// kendall ariel

package com.example.appmetrologica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmetrologica.room.AppDatabase
import com.example.appmetrologica.room.LecturaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LecturaViewModel(application: Application) : AndroidViewModel(application)  { // app android

    // Instancia DAO
    private val dao = AppDatabase.getDatabase(application).lecturaDao()

    // Flow Estate para actualizar la base de datos con la lista de lecturas sin tener que reiniciar
    val lecturas: StateFlow<List<LecturaEntity>> = dao.getTodasLasLecturas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()

        )

    // Guardar nueva lectura v

    fun agregarLectura(temperatura: Double?, valor: Double?= null, longitud: Double?, latitud: Double?) {
        viewModelScope.launch {
            val nuevaLectura = LecturaEntity( // Crea una nueva Lectura usando la clase con los valores
                temperatura = temperatura,
                latitud = latitud,
                longitud = longitud
                // FECHAHORA SYSTEM TIME
            )
            dao.insertarLectura(nuevaLectura)
        }
    }

    // Para borrar una lectura
    fun borrarLectura(lectura: LecturaEntity) {
        viewModelScope.launch {
            dao.eliminarLectura(lectura) // Llama a el objeto (lectura) dentro de la funcion y usa la operacion para eliminar
        }
    }

}