package com.example.appmetrologica.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta es la ESTRUCTURA GENERAL DE LA TABLA DE REGISTROS
@Entity(tableName = "lecturas")
data class LecturaEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Generacion del obj
    val temperatura: Double? = null,
    val longitud: Double? = null,
    val latitud: Double? = null,
    val fechaHora: Long = System.currentTimeMillis()
)