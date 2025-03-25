package com.example.E_Dentogram.controller


import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.request.PatientRequest
import com.example.E_Dentogram.service.PatientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PatientController {

    @Autowired
    lateinit var service: PatientService


    @GetMapping("/allPatients")
    fun allPatients(): ResponseEntity<List<Patient>> {
        val patients = service.allPatients()
        return ResponseEntity.ok(patients)
    }

    @GetMapping("/patient/{patientMedicalRecord}")
    fun getPatient(@PathVariable patientMedicalRecord: Int): ResponseEntity<Patient> {
        val patient = service.getPatient(patientMedicalRecord)
        return ResponseEntity.ok(patient)
    }

    @PostMapping("/patient/{patientMedicalRecord}")
    fun createPatient(@RequestBody patientRequest: PatientRequest): ResponseEntity<PatientDTO> {
        val patientDTO = service.createPatient(patientRequest)
        return ResponseEntity(patientDTO, HttpStatus.CREATED)
    }


}