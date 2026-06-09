package com.cardiolog.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BloodPressureMeasurementEntity::class], version = 1, exportSchema = true)
abstract class CardioLogDatabase : RoomDatabase() {
    abstract fun bloodPressureDao(): BloodPressureDao

    companion object {
        @Volatile private var instance: CardioLogDatabase? = null

        fun getInstance(context: Context): CardioLogDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CardioLogDatabase::class.java,
                "cardiolog.db",
            ).build().also { instance = it }
        }
    }
}
