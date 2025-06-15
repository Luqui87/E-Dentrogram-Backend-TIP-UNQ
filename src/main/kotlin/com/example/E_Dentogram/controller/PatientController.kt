package com.example.E_Dentogram.controller


import com.example.E_Dentogram.dto.*
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.model.PatientJournal
import com.example.E_Dentogram.service.PatientService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = arrayOf("http://localhost:5174"))
@RestController
class PatientController {

    @Autowired
    lateinit var service: PatientService


    @Operation(summary = "Get all Patients")
    @GetMapping("/allPatients") //Borrar
    fun allPatients(): ResponseEntity<List<Patient>> {
        val patients = service.allPatients()
        return ResponseEntity.ok(patients)
    }

    @Operation(summary = "Get all Patients without their teeth")
    @GetMapping("/allSimplePatients") //Borrar
    fun allSimplePatients(): ResponseEntity<List<PatientDTO>> {
        val patients = service.allSimplePatients()
        return ResponseEntity.ok(patients)
    }

    @Operation(summary = "The patient with the medical record")
    @GetMapping("/patient/{patientMedicalRecord}")
    fun getPatient(@PathVariable patientMedicalRecord: Int): ResponseEntity<Patient> {
        val patient = service.getPatient(patientMedicalRecord)
        return ResponseEntity.ok(patient)
    }

    @Operation(summary = "The patient with the medical record")
    @GetMapping("/patient/records/{patientMedicalRecord}/{pageNumber}")
    fun getPatientRecords(@PathVariable patientMedicalRecord: Int,@PathVariable pageNumber:Int): ResponseEntity<PatientRecordDTO> {
        val patientRecord = service.getPatientRecords(patientMedicalRecord,pageNumber)
        return ResponseEntity.ok(patientRecord)
    }

    @Operation(summary = "The patient with the medical record")
    @GetMapping("/patient/journal/{patientMedicalRecord}/{pageNumber}")
    fun getPatientJournal(@PathVariable patientMedicalRecord: Int,@PathVariable pageNumber:Int): ResponseEntity<JournalDTO> {
        val patientRecord = service.getPatientJournal(patientMedicalRecord,pageNumber)
        return ResponseEntity.ok(patientRecord)
    }

    @Operation(summary = "The add entry to the journal to patient with the medical record")
    @PostMapping("/patient/journal/add/{patientMedicalRecord}")
    fun postPatientJournal(@PathVariable patientMedicalRecord: Int,@RequestBody journalPatientRequest: PatientJournalRequest): ResponseEntity<PatientJournal> {
        val patientRecord = service.postPatientJournal(patientMedicalRecord,journalPatientRequest)
        return ResponseEntity.ok(patientRecord)
    }


}