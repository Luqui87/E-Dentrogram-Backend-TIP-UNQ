package com.example.E_Dentogram.dto

import jakarta.annotation.Generated
import java.time.LocalDate


@Generated
class PatientDTO(
    val medicalRecord: Int,
    val dni: Int,
    val name: String,
    val address: String,
    val birthdate: LocalDate,
    val telephone: Int,
    val email: String
    ){}