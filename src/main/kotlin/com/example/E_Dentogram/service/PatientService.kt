package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.request.PatientRequest
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Generated
@Service
@Transactional
class PatientService {

    @Autowired
    lateinit var patientRepository : PatientRepository

    fun allPatients(): List<Patient>? {
        val patient = patientRepository.findAll()
        return patient
    }

    fun getPatient(patientMedicalRecord: Int): Patient {
        return patientRepository.findById(patientMedicalRecord).
        orElseThrow{ throw RuntimeException("patient $patientMedicalRecord not found")
        }
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
            throw RuntimeException("Failed to create patient: ${e.message}")
        }

    }
}
