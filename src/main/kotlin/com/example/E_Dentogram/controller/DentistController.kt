package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.*
import com.example.E_Dentogram.service.DentistService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = arrayOf("http://localhost:5174"))
@RestController
class DentistController {

    @Autowired
    lateinit var service: DentistService


    @Operation(summary = "Get all Dentist")
    @GetMapping("/allDentist")  //Borrar
    fun allDentist(): ResponseEntity<List<DentistSimpleDTO>> {
        val dentists = service.allDentist()
        return ResponseEntity.ok(dentists)
    }

    @Operation(summary = "Get Dentist")
    @GetMapping("/dentist/user")
    fun getDentist(@RequestHeader("Authorization") token: String): ResponseEntity<DentistDTO> {

        val dentist = service.getDentist(token)
        return ResponseEntity.ok(dentist)
    }

    @Operation(summary = "Remove Dentist´s patient")
    @PutMapping("/dentist/Remove/{dentistId}/{patientMedicalRecord}")
    fun removePatient(@PathVariable dentistId: Long,@PathVariable patientMedicalRecord: Int): ResponseEntity<Void> {
        service.removePatient(dentistId,patientMedicalRecord)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "add patient to Dentist ")
    @PostMapping("/dentist/add/{dentistId}")
    fun addPatient(@PathVariable dentistId: Long,@RequestBody patientDTO: PatientDTO): ResponseEntity<DentistDTO> {
        val dentist = service.addPatient(dentistId,patientDTO)
        return ResponseEntity.ok(dentist)
    }

    @Operation(summary = "Add a Dentist to the system")
    @PostMapping("/register")
    fun signUp(@RequestBody dentistDTO: DentistSimpleDTO): ResponseEntity<AuthenticationResponse> {
        val token = service.signUp(dentistDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(token)
    }


}