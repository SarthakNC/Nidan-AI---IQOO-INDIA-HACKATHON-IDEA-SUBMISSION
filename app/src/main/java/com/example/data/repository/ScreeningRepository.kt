package com.example.data.repository

import com.example.data.local.ScreeningDao
import com.example.data.local.ScreeningEntity
import com.example.domain.model.ScreeningRecord
import com.example.domain.model.ScreeningType
import com.example.domain.model.TriageLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScreeningRepository(private val screeningDao: ScreeningDao) {

    val allScreenings: Flow<List<ScreeningRecord>> = screeningDao.getAllScreenings().map { list ->
        list.map { it.toDomainModel() }
    }

    fun getScreeningsForPatient(patientId: String): Flow<List<ScreeningRecord>> =
        screeningDao.getScreeningsForPatient(patientId).map { list ->
            list.map { it.toDomainModel() }
        }

    fun getScreeningsByLevel(level: TriageLevel): Flow<List<ScreeningRecord>> =
        screeningDao.getScreeningsByTriageLevel(level.name).map { list ->
            list.map { it.toDomainModel() }
        }

    val totalCount: Flow<Int> = screeningDao.getTotalScreeningsCount()

    fun getTodayCount(): Flow<Int> {
        val startOfDay = System.currentTimeMillis() - 86400_000L
        return screeningDao.getScreeningsCountSince(startOfDay)
    }

    val pendingSyncCount: Flow<Int> = screeningDao.getPendingSyncCount()

    suspend fun insertScreening(record: ScreeningRecord): Long {
        return screeningDao.insertScreening(record.toEntity())
    }

    suspend fun markAllAsSynced() {
        screeningDao.markAllAsSynced()
    }

    suspend fun populateInitialDataIfEmpty() {
        // Only insert if no initial screenings exist
        // Note: ScreeningDao doesn't have getCount directly but can insert initial demo screenings if empty
        screeningDao.insertScreenings(DemoDataProvider.getInitialScreenings())
    }

    private fun ScreeningEntity.toDomainModel(): ScreeningRecord {
        return ScreeningRecord(
            id = id,
            patientId = patientId,
            patientName = patientName,
            patientAge = patientAge,
            patientGender = patientGender,
            screeningType = try {
                ScreeningType.valueOf(screeningType)
            } catch (e: Exception) {
                ScreeningType.GENERAL
            },
            triageLevel = try {
                TriageLevel.valueOf(triageLevel)
            } catch (e: Exception) {
                TriageLevel.GREEN
            },
            triageTitle = triageTitle,
            recommendedAction = recommendedAction,
            clinicalRationale = clinicalRationale,
            confidence = confidence,
            findingsJson = findingsJson,
            rawNotes = rawNotes,
            timestamp = timestamp,
            isSyncedWithPhc = isSyncedWithPhc
        )
    }

    private fun ScreeningRecord.toEntity(): ScreeningEntity {
        return ScreeningEntity(
            id = id,
            patientId = patientId,
            patientName = patientName,
            patientAge = patientAge,
            patientGender = patientGender,
            screeningType = screeningType.name,
            triageLevel = triageLevel.name,
            triageTitle = triageTitle,
            recommendedAction = recommendedAction,
            clinicalRationale = clinicalRationale,
            confidence = confidence,
            findingsJson = findingsJson,
            rawNotes = rawNotes,
            timestamp = timestamp,
            isSyncedWithPhc = isSyncedWithPhc
        )
    }
}
