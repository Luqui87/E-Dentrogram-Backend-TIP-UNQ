package com.example.E_Dentogram.dto

import com.example.E_Dentogram.model.Patient
import org.springframework.data.domain.Page

data class PatientPaginationDTO(
    val patients: List<PatientDTO>,
    val pageSize: Int,
    val total: Long
) {
    companion object {
        fun fromModel(patients: Page<Patient>): PatientPaginationDTO {
            return PatientPaginationDTO(
                patients = patients.content.map { patient : Patient ->
                    PatientDTO(
                        medicalRecord = patient.medicalRecord!!,
                        dni = patient.dni!!,
                        name = patient.name!!,
                        address = patient.address!!,
                        birthdate = patient.birthdate!!,
                        telephone = patient.telephone!!,
                        email = patient.email!!
                    )
                },
                pageSize = patients.size,
                total = patients.totalElements
            )
        }
    }
}
