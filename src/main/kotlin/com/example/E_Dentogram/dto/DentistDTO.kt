package com.example.E_Dentogram.dto

import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Patient

class DentistDTO(
    val dentistID: Long,
    val username: String,
    val email: String,
    val patients: List<PatientDTO>
){

    companion object{
        fun fromModel(dentist: Dentist): DentistDTO{
            val patientDTOs = dentist.patients!!.map {
                    pat: Patient ->
                PatientDTO(
                    medicalRecord = pat.medicalRecord!!,
                    dni = pat.dni!!,
                    name = pat.name!!,
                    address = pat.address!!,
                    birthdate = pat.birthdate!!,
                    telephone = pat.telephone!!,
                    email = pat.email!!)}

            val dentistdto = DentistDTO(
                dentistID = dentist.id!!,
                username = dentist.username!!,
                email = dentist.email!!,
                patients = patientDTOs
            )
            return dentistdto
        }
    }

}