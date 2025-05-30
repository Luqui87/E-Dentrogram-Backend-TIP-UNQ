package com.example.E_Dentogram.service

import com.example.E_Dentogram.config.JwtProperties
import com.example.E_Dentogram.dto.AuthenticationRequest
import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.dto.GoogleTokenDTO
import com.example.E_Dentogram.repository.DentistRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailService: CustomUserDetailService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val dentistRepository: DentistRepository
) {
    @Value("\${google.client.clientId}")
    private val clientId: String? = null

    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.username,
                authRequest.password
            )
        )

        val user = userDetailService.loadUserByUsername(authRequest.username)
        val accessToken = tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return AuthenticationResponse(
            accessToken = accessToken
        )
    }

    fun authenticationGoogle(googleToken: GoogleTokenDTO): AuthenticationResponse {


        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(clientId))
            .build()

        return try {
            val idToken = verifier.verify(googleToken.token)
            if (idToken != null){
                val payload = idToken.payload
                val email = payload.email

                val user = userDetailService.loadUserByUsername(email)
                val accessToken = tokenService.generate(
                    userDetails = user,
                    expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
                )

                 AuthenticationResponse(accessToken = accessToken)
            }
            else{
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
            }
        }
        catch (e: Exception){
            throw  ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")
        }


    }
}
