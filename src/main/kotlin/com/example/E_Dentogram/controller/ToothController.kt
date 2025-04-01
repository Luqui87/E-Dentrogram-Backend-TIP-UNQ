package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.ToothDTO
import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.request.ToothRequest
import com.example.E_Dentogram.service.ToothService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ToothController{

    @Autowired
    lateinit var service: ToothService

    @GetMapping("/allTooth")
    fun allTooth(): ResponseEntity<List<ToothDTO>> {
        val teeth = service.allTooth()
        return ResponseEntity.ok(teeth)
    }

    @GetMapping("/tooth/{medicalRecord}")
    fun teeth(@PathVariable medicalRecord: Int): ResponseEntity<List<ToothDTO>> {
        val teeth = service.teeth(medicalRecord)
        return ResponseEntity.ok(teeth)
    }

    @PutMapping("/update/tooth/{medicalRecord}")
    fun updateTeeth(@PathVariable medicalRecord: Int,@RequestBody teethRequests: List<ToothRequest>): List<ToothDTO> {
        val updatedTeeth = service.updateTeeth(medicalRecord,teethRequests)
        return updatedTeeth
    }



}