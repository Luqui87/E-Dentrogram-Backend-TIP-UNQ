package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.AuthenticationRequest
import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.service.AuthenticationService
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.GeneralSecurityException
import java.util.*


@RestController
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @Value("\${google.client.clientId}")
    private val clientId: String? = null

    @PostMapping("/login")
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse =
        authenticationService.authentication(authRequest)

    @PostMapping("/login/google")
    fun authenticateWithGoogle(@RequestBody tokenString:String): ResponseEntity<AuthenticationResponse>{
        val idTokenString = tokenString.replace("\"", "")

        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(clientId))
            .build()

        return try {
            val idToken = verifier.verify(idTokenString)
            if (idToken != null){
                val payload = idToken.payload
                val email = payload.email

                ResponseEntity.ok(authenticationService.authenticationGoogle(email))
            }
            else{
                ResponseEntity(HttpStatus.UNAUTHORIZED)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}