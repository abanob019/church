//package com.azmiradi.churchapp.local_data
//
//import androidx.room.*
//import com.azmiradi.churchapp.all_applications.ApplicationPojo
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface ApplicationDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addApplication(applicationPojo: ApplicationPojo)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addListApplication(applications: List<ApplicationPojo>)
//
//    @Query("SELECT * FROM applicationPojo ORDER BY priority DESC")
//    fun getApplications(): Flow<List<ApplicationPojo>>
//
//    @Update
//    suspend fun updateApplications(applicationPojo: ApplicationPojo)
//
//    @Delete
//    suspend fun deleteApplications(applicationPojo: ApplicationPojo)
//
//}