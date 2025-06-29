package com.example.E_Dentogram.controller


import com.example.E_Dentogram.dto.TurnRequest
import com.example.E_Dentogram.model.Turn
import com.example.E_Dentogram.service.TurnService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@CrossOrigin(origins = ["http://localhost:5174"])
@RestController
class TurnController {

    @Autowired
    lateinit var service: TurnService

    @Operation(summary = "Add a turn")
    @PostMapping("/turn/add")
    fun addTurn(@RequestBody request: TurnRequest): ResponseEntity<Turn> {
        val turn = service.addTurn(request)
        return ResponseEntity.ok(turn)
    }

    @Operation(summary = "Add a turn")
    @PostMapping("/turn/reschedule/{newDate}")
    fun rescheduleTurn(@RequestBody request: TurnRequest,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) newDate: LocalDateTime): ResponseEntity<Turn> {
        val turn = service.rescheduleTurn(request,newDate)
        return ResponseEntity.ok(turn)
    }

}