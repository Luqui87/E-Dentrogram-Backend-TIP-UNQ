package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Patient
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable

@Repository
interface PatientRepository : JpaRepository<Patient, Int> {

    fun findByDentistUsername(username: String, pageable: Pageable): Page<Patient>

    fun findByDentistUsernameAndNameContainingIgnoreCase(username: String, name: String, pageable: Pageable): Page<Patient>

}