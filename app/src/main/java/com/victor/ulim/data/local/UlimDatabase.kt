package com.victor.ulim.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserEntity::class, StudentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UlimDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao

    companion object {
        /** Hardcoded demo credentials, seeded when the database is first created. */
        const val DEMO_USERNAME = "admin"
        const val DEMO_PASSWORD = "admin123"

        @Volatile
        private var instance: UlimDatabase? = null

        fun get(context: Context): UlimDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UlimDatabase::class.java,
                    "ulim.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL(
                                "INSERT INTO users (username, password) VALUES (?, ?)",
                                arrayOf(DEMO_USERNAME, DEMO_PASSWORD)
                            )
                        }
                    })
                    .build()
                    .also { instance = it }
            }
    }
}
