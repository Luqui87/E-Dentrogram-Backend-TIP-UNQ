package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.AuthenticationRequest
import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/auth")
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse =
        authenticationService.authentication(authRequest)

}