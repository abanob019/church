package com.azmiradi.churchapp.local_database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Zone(
    @PrimaryKey(autoGenerate = true)
    var zoneID: Int = 0,
    val zoneName: String? = null,
    val zoneColor: String? = null,
    val code:String?=""
)