package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.DentistDTO
import com.example.E_Dentogram.dto.DentistSimpleDTO
import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Generated
@Service
@Transactional
class DentistService {

    @Autowired
    lateinit var dentistRepository : DentistRepository

    @Transactional(readOnly=true)
    fun allDentist(): List<DentistSimpleDTO>? {
        val dentists = dentistRepository.findAll()

        val dentistDTOs = dentists.map {
            dentist -> DentistSimpleDTO(
                username = dentist.username!!,
                password = dentist.password!!)
        }
        return dentistDTOs
    }

    @Transactional(readOnly=true)
    fun getDentist(username: String): DentistDTO {

        val dentist = dentistRepository.findById(username).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }

        val patientDTOs = dentist.patients!!.map {
            patient: Patient ->
                PatientDTO(
                    medicalRecord = patient.medicalRecord!!,
                    dni = patient.dni!!,
                    name = patient.name!!,
                    address = patient.address!!,
                    birthdate = patient.birthdate!!,
                    telephone = patient.telephone!!,
                    email = patient.email!!)}

        val dentistdto = DentistDTO(
                username = dentist.username!!,
                password = dentist.password!!,
                patients = patientDTOs
        )

        return dentistdto
    }

}