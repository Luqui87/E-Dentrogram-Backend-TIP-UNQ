package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.model.Tooth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : JpaRepository<Patient, Int> {

}