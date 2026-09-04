package com.example.appmetrologica.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// OPERACIONES QUE SE HARAN EN LA BASE DE DATOS
@Dao
interface LecturaDao {

    @Query("SELECT * FROM lecturas ORDER BY fechaHora DESC")
    fun getTodasLasLecturas(): Flow<List<LecturaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLectura(lectura: LecturaEntity)

    @Delete
    suspend fun eliminarLectura(lectura: LecturaEntity)

    @Query("DELETE FROM lecturas")
    suspend fun borrarTodo()
}
// corrutinas suspend fun