package com.example.E_Dentogram.controller

import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.service.ToothService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ToothController{

    @Autowired
    lateinit var service: ToothService

    @GetMapping("/allTooth")
    fun allTooth(): ResponseEntity<List<Tooth>> {
        val teeth = service.allTooth()
        return ResponseEntity.ok(teeth)
    }

}