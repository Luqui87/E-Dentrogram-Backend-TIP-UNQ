package com.example.E_Dentogram.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class testController {

    @GetMapping("/test")
    fun test(): String {
        return "Hola"
    }

}