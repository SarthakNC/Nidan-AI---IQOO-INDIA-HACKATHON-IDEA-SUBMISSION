package com.example.data.repository

import com.example.data.local.PatientDao
import com.example.data.local.PatientEntity
import com.example.domain.model.Patient
import com.example.domain.model.TriageLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PatientRepository(private val patientDao: PatientDao) {

    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients().map { entities ->
        entities.map { it.toDomainModel() }
    }

    fun searchPatients(query: String): Flow<List<Patient>> =
        patientDao.searchPatients(query).map { entities ->
            entities.map { it.toDomainModel() }
        }

    suspend fun getPatientById(patientId: String): Patient? {
        return patientDao.getPatientById(patientId)?.toDomainModel()
    }

    suspend fun insertPatient(patient: Patient): Long {
        return patientDao.insertPatient(patient.toEntity())
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient.toEntity())
    }

    suspend fun populateInitialDataIfEmpty() {
        if (patientDao.getPatientCount() == 0) {
            patientDao.insertPatients(DemoDataProvider.getInitialPatients())
        }
    }

    private fun PatientEntity.toDomainModel(): Patient {
        return Patient(
            id = id,
            patientId = patientId,
            name = name,
            age = age,
            gender = gender,
            villageArea = villageArea,
            abhaId = abhaId,
            latestTriageLevel = try {
                TriageLevel.valueOf(latestTriageLevel)
            } catch (e: Exception) {
                TriageLevel.GREEN
            },
            totalScreenings = totalScreenings,
            createdAt = createdAt
        )
    }

    private fun Patient.toEntity(): PatientEntity {
        return PatientEntity(
            id = id,
            patientId = patientId,
            name = name,
            age = age,
            gender = gender,
            villageArea = villageArea,
            abhaId = abhaId,
            latestTriageLevel = latestTriageLevel.name,
            totalScreenings = totalScreenings,
            createdAt = createdAt
        )
    }
}
