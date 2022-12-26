package com.azmiradi.churchapp.local_data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.azmiradi.churchapp.all_applications.ApplicationPojo

@Database(
    entities = [ApplicationPojo::class],
    version = 1,
    exportSchema = true
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun applicationDao(): ApplicationDao
    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null
        fun getDatabase(context: Context): NoteDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): NoteDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "applications_database"
            ).build()
        }
    }
}