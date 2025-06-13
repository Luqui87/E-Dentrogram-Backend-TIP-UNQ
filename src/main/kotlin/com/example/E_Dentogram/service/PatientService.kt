package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.dto.PatientRecordDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.model.PatientRecord
import com.example.E_Dentogram.repository.PatientRecordRepository
import com.example.E_Dentogram.repository.PatientRepository
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    @Autowired
    lateinit var patientRecordRepository : PatientRecordRepository

    @Transactional(readOnly=true)
    fun allPatients(): List<Patient>? {
        val patients = patientRepository.findAll()
        return patients
    }

    @Transactional(readOnly=true)
    fun getPatient(patientMedicalRecord: Int): Patient {
        return patientRepository.findById(patientMedicalRecord).
            orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist") }
    }


    @Transactional(readOnly=true)
    fun allSimplePatients(): List<PatientDTO>? {
        val patients = patientRepository.findAll()

        return patients.map { patient -> PatientDTO(
            medicalRecord = patient.medicalRecord!!,
            dni = patient.dni!!,
            name = patient.name!!,
            address = patient.address!!,
            birthdate = patient.birthdate!!,
            telephone = patient.telephone!!,
            email = patient.email!!) }
    }

    @Transactional
    fun getPatientRecords(patientMedicalRecord: Int, pageNumber:Int): PatientRecordDTO {
        val pageRequest = PageRequest.of(pageNumber, 10, Sort.by(Sort.Direction.DESC, "date"))
        val page = patientRecordRepository.findByPatient_MedicalRecord(patientMedicalRecord, pageRequest)

        val patientRecordDTO = PatientRecordDTO(page.content,page.totalElements )
        return patientRecordDTO
    }


}
