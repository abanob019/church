package com.azmiradi.churchapp.local_database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.azmiradi.churchapp.all_applications.ApplicationPojo

@Database(
    entities = [ApplicationPojo::class, Zone::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationDao(): ApplicationsDao
    abstract fun zoneDao(): ZoneDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(application: Application): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(application)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(application: Application): AppDatabase {
            return Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                "applications_database"
            ).build()
        }
    }
}