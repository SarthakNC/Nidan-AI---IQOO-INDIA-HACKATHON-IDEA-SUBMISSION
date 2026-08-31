package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreeningDao {
    @Query("SELECT * FROM screenings ORDER BY timestamp DESC")
    fun getAllScreenings(): Flow<List<ScreeningEntity>>

    @Query("SELECT * FROM screenings WHERE patientId = :patientId ORDER BY timestamp DESC")
    fun getScreeningsForPatient(patientId: String): Flow<List<ScreeningEntity>>

    @Query("SELECT * FROM screenings WHERE triageLevel = :level ORDER BY timestamp DESC")
    fun getScreeningsByTriageLevel(level: String): Flow<List<ScreeningEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreening(screening: ScreeningEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenings(screenings: List<ScreeningEntity>)

    @Query("SELECT COUNT(*) FROM screenings")
    fun getTotalScreeningsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM screenings WHERE timestamp >= :sinceTimestamp")
    fun getScreeningsCountSince(sinceTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM screenings WHERE isSyncedWithPhc = 0")
    fun getPendingSyncCount(): Flow<Int>

    @Query("UPDATE screenings SET isSyncedWithPhc = 1 WHERE isSyncedWithPhc = 0")
    suspend fun markAllAsSynced()
}
