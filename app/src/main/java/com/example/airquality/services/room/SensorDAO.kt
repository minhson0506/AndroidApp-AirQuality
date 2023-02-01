package com.example.airquality.services.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SensorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: SensorModel)

    @Delete
    fun delete(data: SensorModel)

    @Update
    fun update(data: SensorModel)

    @Query("SELECT * FROM sensormodel")
    fun getAll(): LiveData<List<SensorModel>>

    @Query("SELECT * FROM sensormodel WHERE id = ( SELECT MAX(id) FROM sensormodel)")
    fun getLatest(): LiveData<SensorModel>

    @Query("SELECT * FROM sensormodel WHERE time LIKE :date")
    fun getDataInDate(date: String): LiveData<List<SensorModel>>
}