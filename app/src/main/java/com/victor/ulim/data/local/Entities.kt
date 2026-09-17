package com.victor.ulim.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Account used by the login screen ("1 pagina de logare verifica parola"). */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String
)

/** Form data ("salveaza in BD daca sunt toate campurile completate"). */
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nume: String,
    val prenume: String,
    val dataNastere: String,
    val gen: String,
    val listaStudii: String,
    val specialitatea: String,
    val fotoUri: String
)
