package com.example.E_Dentogram.repository

import com.example.E_Dentogram.dto.ToothDTO
import com.example.E_Dentogram.model.PatientRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

interface PatientRecordRepository : JpaRepository<PatientRecord, Long> {
    fun findByPatient_MedicalRecord(medicalRecord: Int, pageable: Pageable): Page<PatientRecord>

    @Query(
        value = """
        SELECT DISTINCT ON (record_table.tooth_number) *
        FROM record_table
        WHERE record_table.patient_id = :patientId
          AND record_table.date <= :givenDateTime
        ORDER BY record_table.tooth_number, record_table.date desc

    """,
        nativeQuery = true
    )
    fun findLatestToothRecordsUpToDate(
        @Param("patientId") patientId: Int,
        @Param("givenDateTime") givenDateTime: Timestamp
    ): List<PatientRecord>

}