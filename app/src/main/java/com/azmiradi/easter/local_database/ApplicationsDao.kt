package com.azmiradi.easter.local_database

import androidx.room.*
import com.azmiradi.easter.all_applications.ApplicationPojo
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addApplication(applicationPojo: ApplicationPojo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addApplications(applicationPojo: List<ApplicationPojo>)

    @Query("SELECT * FROM applicationpojo")
    fun getApplications(): Flow<List<ApplicationPojo>>

    @Query("SELECT * FROM applicationpojo where nationalID= :nationalID")
    fun getApplication(nationalID: String): Flow<List<ApplicationPojo>>

    @Query("SELECT * FROM applicationpojo where invitationNumber= :invitationID")
    fun getApplicationByInvitationID(invitationID: String): Flow<List<ApplicationPojo>>

    @Update
    suspend fun updateApplications(applicationPojo: ApplicationPojo)

    @Delete
    suspend fun deleteApplications(applicationPojo: ApplicationPojo)

    @Query("DELETE FROM applicationpojo")
    suspend fun deleteApplications()
}