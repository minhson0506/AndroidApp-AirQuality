package com.example.airquality.services.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SensorModel::class], version = 4)
abstract class RoomDB: RoomDatabase() {
    abstract fun sensorDao(): SensorDAO

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getInstance( context: Context): RoomDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDB::class.java,
                        "sensor.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}