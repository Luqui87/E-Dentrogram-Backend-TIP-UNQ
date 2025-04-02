package com.example.E_Dentogram.controller


import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.request.PatientRequest
import com.example.E_Dentogram.service.PatientService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PatientController {

    @Autowired
    lateinit var service: PatientService


    @Operation(summary = "Get all Patients")
    @GetMapping("/allPatients")
    fun allPatients(): ResponseEntity<List<Patient>> {
        val patients = service.allPatients()
        return ResponseEntity.ok(patients)
    }

    @Operation(summary = "Get all Patients without their teeth")
    @GetMapping("/allSimplePatients")
    fun allSimplePatients(): ResponseEntity<List<PatientRequest>> {
        val patients = service.allSimplePatients()
        return ResponseEntity.ok(patients)
    }

    @Operation(summary = "The patient with the medical record")
    @GetMapping("/patient/{patientMedicalRecord}")
    fun getPatient(@PathVariable patientMedicalRecord: Int): ResponseEntity<Patient> {
        val patient = service.getPatient(patientMedicalRecord)
        return ResponseEntity.ok(patient)
    }

    @Operation(summary = "Register patient")
    @PostMapping("/patient/{patientMedicalRecord}")
    fun createPatient(@RequestBody patientRequest: PatientRequest): ResponseEntity<PatientDTO> {
        val patientDTO = service.createPatient(patientRequest)
        return ResponseEntity(patientDTO, HttpStatus.CREATED)
    }


}