package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.PatientRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PatientRecordRepository : JpaRepository<PatientRecord, Long> {
    fun findByPatient_MedicalRecord(medicalRecord: Int, pageable: Pageable): Page<PatientRecord>

}