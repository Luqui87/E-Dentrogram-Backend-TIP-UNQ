package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.DentistDTO
import com.example.E_Dentogram.dto.DentistSimpleDTO
import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
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
    @Autowired
    lateinit var patientRepository : PatientRepository

    @Transactional(readOnly=true)
    fun allDentist(): List<DentistSimpleDTO>? {
        val dentists = dentistRepository.findAll()

        val dentistDTOs = dentists.map {
            dentist -> DentistSimpleDTO(
                dentistID = dentist.id!!,
                username = dentist.username!!,
                password = dentist.password!!)
        }
        return dentistDTOs
    }

    @Transactional(readOnly=true)
    fun getDentist(dentistId: Long): DentistDTO {

        val dentist = dentistRepository.findById(dentistId).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }

        return DentistDTO.fromModel(dentist)
    }

    fun removePatient(dentistId: Long,patientMedicalRecord: Int) {
        val dentist = dentistRepository.findById(dentistId).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }

        try {
            dentist.removePatient(patientMedicalRecord)

            dentistRepository.save(dentist)
        }catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to save changes: ${e.message}")
        }

    }

    fun addPatient(dentistId: Long, patientDTO: PatientDTO): DentistDTO {
        val dentist = dentistRepository.findById(dentistId).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }

        var patient = patientRepository.findById(patientDTO.medicalRecord).orElse(null)

        if (patient == null) {
            val newPatient = try {
                Patient.PatientBuilder()
                    .medicalRecord(patientDTO.medicalRecord)
                    .dni(patientDTO.dni)
                    .name( patientDTO.name)
                    .address(patientDTO.address)
                    .birthdate(patientDTO.birthdate)
                    .telephone(patientDTO.telephone)
                    .email(patientDTO.email)
                    .teeth(mutableListOf())
                    .build()
            } catch (e: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data provided for register patient : ${e.message}", e)
            }
            patient = newPatient
        }

        dentist.addPatient(patient)

        try {
            dentistRepository.save(dentist)
        }catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to save changes: ${e.message}")
        }

        return DentistDTO.fromModel(dentist)

    }

}