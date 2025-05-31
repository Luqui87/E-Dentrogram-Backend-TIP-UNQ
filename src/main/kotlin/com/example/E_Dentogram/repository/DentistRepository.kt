package com.example.E_Dentogram.repository

import com.example.E_Dentogram.dto.DentistDTO
import com.example.E_Dentogram.model.Dentist
import org.springframework.data.jpa.repository.JpaRepository

interface DentistRepository : JpaRepository<Dentist, Long> {

    fun existsDentistByUsername(username: String) : Boolean

    fun findByEmail(username: String): Dentist?

    fun findByUsername(username: String): Dentist?

    fun findByUsernameOrEmail(username: String, email: String): Dentist?

    fun existsDentistByEmail(email: String): Boolean
}