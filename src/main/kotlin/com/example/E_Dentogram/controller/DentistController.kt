package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.DentistDTO
import com.example.E_Dentogram.dto.DentistSimpleDTO
import com.example.E_Dentogram.service.DentistService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = arrayOf("http://localhost:5174"))
@RestController
class DentistController {

    @Autowired
    lateinit var service: DentistService

    @Operation(summary = "Get all Dentist")
    @GetMapping("/allDentist")
    fun allDentist(): ResponseEntity<List<DentistSimpleDTO>> {
        val dentists = service.allDentist()
        return ResponseEntity.ok(dentists)
    }

    @Operation(summary = "Get Dentist")
    @GetMapping("/dentist/{username}")
    fun getDentist(@PathVariable username: String): ResponseEntity<DentistDTO> {
        val dentist = service.getDentist(username)
        return ResponseEntity.ok(dentist)
    }


}