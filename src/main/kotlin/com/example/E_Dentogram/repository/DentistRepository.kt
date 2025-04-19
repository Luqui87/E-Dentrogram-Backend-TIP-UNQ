package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Dentist
import org.springframework.data.jpa.repository.JpaRepository

interface DentistRepository : JpaRepository<Dentist, Long> {

    fun existsDentistByUsername(username: String) : Boolean

    fun findByUsername(username: String): Dentist?
}