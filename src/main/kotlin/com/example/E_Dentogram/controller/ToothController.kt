package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.ToothDTO
import com.example.E_Dentogram.service.ToothService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@CrossOrigin(origins = ["http://localhost:5174"])
@RestController
class ToothController{

    @Autowired
    lateinit var service: ToothService

    @Operation(summary = "Get all teeth")
    @GetMapping("/allTooth")
    fun allTooth(): ResponseEntity<List<ToothDTO>> {
        val teeth = service.allTooth()
        return ResponseEntity.ok(teeth)
    }

    @Operation(summary = "Get all teeth for the patient wiht the medical record")
    @GetMapping("/tooth/{medicalRecord}")
    fun teeth(@PathVariable medicalRecord: Int): ResponseEntity<List<ToothDTO>> {
        val teeth = service.teeth(medicalRecord)
        return ResponseEntity.ok(teeth)
    }

    @Operation(summary = "update teeth ")
    @PutMapping("/update/tooth/{medicalRecord}")
    fun updateTeeth(@PathVariable medicalRecord: Int,@RequestBody teethDTO: List<ToothDTO>): List<ToothDTO> {
        val updatedTeeth = service.updateTeeth(medicalRecord,teethDTO)
        return updatedTeeth
    }



}