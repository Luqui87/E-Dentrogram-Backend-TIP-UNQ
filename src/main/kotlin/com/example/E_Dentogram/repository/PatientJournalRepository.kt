package com.example.E_Dentogram.repository
import com.example.E_Dentogram.model.PatientJournal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface PatientJournalRepository : JpaRepository<PatientJournal, Long> {
    fun findByPatient_MedicalRecord(medicalRecord: Int, pageable: Pageable): Page<PatientJournal>
}


