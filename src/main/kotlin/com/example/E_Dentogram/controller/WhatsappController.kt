package com.example.E_Dentogram.controller

import com.example.E_Dentogram.service.WhatsappService
import com.example.E_Dentogram.dto.WhatsappRequest
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

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

    @Operation(summary = "Get QR code to connect to WhatsApp")
    @GetMapping("/qr")
    fun getQr(): ResponseEntity<String> {
        val qr = service.getQrCode()
        return ResponseEntity.ok(qr)
    }

    @PostMapping("/send-multiple-files", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun sendMsgWithFiles(@RequestPart("number") number: String, @RequestPart("message") message: String, @RequestPart("files", required = false) files: List<MultipartFile>?): ResponseEntity<String> {
        val tempFiles = files?.map {
            File.createTempFile("upload-", it.originalFilename ?: ".tmp").apply {
                it.transferTo(this)
            }
        } ?: emptyList()

        val response = service.sendMsgWithFiles(number, message, tempFiles)

        tempFiles.forEach { it.delete() }

        return ResponseEntity.ok(response)
    }




}