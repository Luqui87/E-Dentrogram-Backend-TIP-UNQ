package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.*
import com.example.E_Dentogram.service.DentistService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = arrayOf("http://localhost:5174"))
@RestController
class DentistController {

    @Autowired
    private lateinit var dentistService: DentistService

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

    @Operation(summary = "Get pagination dentist´s patient")
    @GetMapping("/dentist/patient/{pageNumber}")
    fun getDentistPatient(@RequestHeader("Authorization") token: String,@PathVariable pageNumber:Int): ResponseEntity<PatientPaginationDTO> {

        val dentist = service.getDentistPatient(token,pageNumber)
        return ResponseEntity.ok(dentist)
    }

    @Operation(summary = "Get pagination dentist´s patient who match")
    @GetMapping("/dentist/patient/{query}/{pageNumber}")
    fun getDentistPatientQuery(@RequestHeader("Authorization") token: String,@PathVariable pageNumber:Int,@PathVariable query:String): ResponseEntity<PatientPaginationDTO> {

        val dentist = service.getDentistPatientQuery(token,pageNumber,query)
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

    @Operation(summary = "Add a Dentist though Google Account")
    @PostMapping("/register/google")
    fun signUpGoogle(@RequestBody googleTokenDTO: GoogleTokenDTO): ResponseEntity<AuthenticationResponse> {
        val accessToken = service.signUpGoogle(googleTokenDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(accessToken)
    }

    @Operation(summary = "Update dentist tags")
    @PatchMapping("/dentist/update/tags")
    fun updateDentistTags(@RequestBody tags: List<String>, @RequestHeader("Authorization") token: String) : ResponseEntity<DentistDTO> {
        val dentist = dentistService.updateTags(tags,token)
        return ResponseEntity.ok().body(dentist)
    }

    @Operation(summary = "Update dentist documents")
    @PatchMapping("/dentist/update/documents")
    fun updateDentistDocuments( @RequestParam("documents") files: List<MultipartFile>, @RequestHeader("Authorization") token: String) : ResponseEntity<DentistDTO> {
        val dentistDTO = dentistService.updateDocuents(files,token)
        return ResponseEntity.ok().body(dentistDTO)
    }

    @Operation(summary = "Delete dentist document")
    @DeleteMapping("/dentist/delete/documents")
    fun deleteDentistDocument( @RequestParam doc: String, @RequestHeader("Authorization") token: String) : ResponseEntity<DentistDTO> {
        val dentistDTO = dentistService.deleteDocument(doc,token)
        return ResponseEntity.ok().body(dentistDTO)
    }

}