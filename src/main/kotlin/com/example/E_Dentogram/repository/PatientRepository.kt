package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : JpaRepository<Patient, Int> {

}