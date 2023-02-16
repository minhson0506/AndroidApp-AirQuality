package com.example.airquality.services.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface SensorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: SensorModel)

    @Delete
    fun delete(data: SensorModel)

    @Update
    fun update(data: SensorModel)

//    @Query("SELECT * FROM sensormodel")
//    fun getAll(): LiveData<List<SensorModel>>

    @Query("SELECT * FROM sensormodel ORDER BY time")
    fun getAll(): DataSource.Factory<Int, SensorModel>

    @Query("SELECT * FROM sensormodel ORDER BY time DESC LIMIT 1")
    fun getLatest(): LiveData<SensorModel>

    @Query("SELECT * FROM sensormodel WHERE time LIKE :date")
    fun getDataInDate(date: String): LiveData<List<SensorModel>>
}