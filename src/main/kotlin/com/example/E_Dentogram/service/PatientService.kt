package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.request.PatientRequest
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Generated
@Service
@Transactional
class PatientService {

    @Autowired
    lateinit var patientRepository : PatientRepository

    @Transactional(readOnly=true)
    fun allPatients(): List<Patient>? {
        val patients = patientRepository.findAll()
        return patients
    }

    @Transactional(readOnly=true)
    fun getPatient(patientMedicalRecord: Int): Patient {
        return patientRepository.findById(patientMedicalRecord).
            // debería llegar un 404, le va a llegar un 500
            // falta tener una excepción que mapee contra un 404
            orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist") }
    }

    fun createPatient(patientRequest: PatientRequest): PatientDTO {
        try {

            val patient =
                Patient.PatientBuilder()
                    .medicalRecord(patientRequest.medicalRecord)
                    .dni(patientRequest.dni)
                    .name( patientRequest.name)
                    .address(patientRequest.address)
                    .birthdate(patientRequest.birthdate)
                    .telephone(patientRequest.telephone)
                    .email(patientRequest.email)
                    .build()

            patientRepository.save(patient)

            val patientDTO = PatientDTO(
                medicalRecord=patientRequest.medicalRecord,
                dni = patientRequest.dni,
                name = patientRequest.name,
                address = patientRequest.address,
                birthdate = patientRequest.birthdate,
                telephone = patientRequest.telephone,
                email = patientRequest.email
                )

            return patientDTO

        }catch (e: Exception) {
        // hay que diferenciar errores de usuario: required => 400
        // vs. errores de BD (se cayó la base) => 500
            throw RuntimeException("Failed to create patient: ${e.message}")
        }

    }

    @Transactional(readOnly=true)
    fun allSimplePatients(): List<PatientRequest>? {
        val patients = patientRepository.findAll()

        return patients.map { patient -> PatientRequest(
            medicalRecord = patient.medicalRecord!!,
            dni = patient.dni!!,
            name = patient.name!!,
            address = patient.address!!,
            birthdate = patient.birthdate!!,
            telephone = patient.telephone!!,
            email = patient.email!!) }
    }
}
