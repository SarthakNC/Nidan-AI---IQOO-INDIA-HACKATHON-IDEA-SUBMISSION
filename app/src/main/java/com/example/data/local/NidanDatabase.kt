package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PatientEntity::class, ScreeningEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NidanDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun screeningDao(): ScreeningDao

    companion object {
        @Volatile
        private var INSTANCE: NidanDatabase? = null

        fun getInstance(context: Context): NidanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NidanDatabase::class.java,
                    "nidan_ai_offline.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
