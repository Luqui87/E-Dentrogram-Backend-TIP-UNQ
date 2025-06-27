package com.example.E_Dentogram.dto

import com.example.E_Dentogram.model.Patient
import jakarta.annotation.Generated
import java.time.LocalDate


@Generated
class PatientDTO(
    val medicalRecord: Int,
    val dni: Int,
    val name: String,
    val address: String,
    val birthdate: LocalDate,
    val telephone: Long,
    val email: String
    ){
    companion object {
        fun fromModel(patient: Patient): PatientDTO =
            PatientDTO(
                medicalRecord = patient.medicalRecord!!,
                dni = patient.dni!!,
                name = patient.name!!,
                address = patient.address!!,
                birthdate = patient.birthdate!!,
                telephone = patient.telephone!!,
                email = patient.email!!
            )
    }
}