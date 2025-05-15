package com.example.E_Dentogram.controller

import com.example.E_Dentogram.service.WhatsappService
import com.example.E_Dentogram.dto.WhatsappRequest
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:5174"])
@RestController
class WhatsappController {

    @Autowired
    lateinit var service: WhatsappService

    @Operation(summary = "send a msg to Whatsapp")
    @PostMapping("/send")
    fun sendMsg(@RequestBody request: WhatsappRequest): ResponseEntity<String> {
        val msg = service.sendMsg(request.number, request.message)
        return ResponseEntity.ok(msg)
    }

}