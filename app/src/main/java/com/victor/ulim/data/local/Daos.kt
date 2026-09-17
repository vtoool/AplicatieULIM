package com.victor.ulim.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun find(username: String, password: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Insert
    suspend fun insert(user: UserEntity)
}

@Dao
interface StudentDao {

    /** Keeps the last saved record so the Display screen can restore it. */
    @Query("SELECT * FROM students ORDER BY id DESC LIMIT 1")
    fun observeLatest(): Flow<StudentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)
}
