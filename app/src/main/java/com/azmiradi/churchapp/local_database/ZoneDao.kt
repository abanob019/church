package com.azmiradi.churchapp.local_database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addZone(zone: Zone)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addZones(zoneList: List<Zone>)

    @Query("SELECT * FROM zone")
    fun getZones(): Flow<List<Zone>>

    @Query("SELECT * FROM zone where zoneName == :zoneName")
    fun getZone(zoneName:String): Flow<List<Zone>>

    @Query("SELECT * FROM zone where zoneID= :id")
    fun getZoneByID(id: String): Flow<List<Zone>>

    @Query("DELETE FROM zone")
    suspend fun deleteZones()
}