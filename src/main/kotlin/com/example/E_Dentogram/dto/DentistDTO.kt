package com.example.E_Dentogram.dto

class DentistDTO(
    val username: String,
    val password: String,
    val patients: List<PatientDTO>
){}