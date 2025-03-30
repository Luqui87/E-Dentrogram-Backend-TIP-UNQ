package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Tooth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThoothRepository : JpaRepository<Tooth,Int> {

    fun findByPatientMedicalRecord(medicalRecord: Int): List<Tooth>

}