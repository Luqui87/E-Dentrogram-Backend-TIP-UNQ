package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Tooth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ToothRepository : JpaRepository<Tooth,Int> {

    fun findByPatientMedicalRecord(medicalRecord: Int): List<Tooth>

    fun findByNumberAndPatientMedicalRecord(medicalRecord: Int, patientId: Int): Tooth?


}