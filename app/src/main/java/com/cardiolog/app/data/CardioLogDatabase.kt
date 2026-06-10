package com.cardiolog.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BloodPressureMeasurementEntity::class, UserProfileEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class CardioLogDatabase : RoomDatabase() {
    abstract fun bloodPressureDao(): BloodPressureDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile private var instance: CardioLogDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE blood_pressure_measurements ADD COLUMN period TEXT NOT NULL DEFAULT 'Morning'")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        age INTEGER,
                        sex TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent(),
                )
            }
        }

        fun getInstance(context: Context): CardioLogDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CardioLogDatabase::class.java,
                "cardiolog.db",
            ).addMigrations(migration1To2).build().also { instance = it }
        }
    }
}
