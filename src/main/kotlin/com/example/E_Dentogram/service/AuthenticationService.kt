package com.example.E_Dentogram.service

import com.example.E_Dentogram.config.JwtProperties
import com.example.E_Dentogram.dto.AuthenticationRequest
import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.repository.DentistRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailService: CustomUserDetailService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val dentistRepository: DentistRepository
) {

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

    fun authenticationGoogle(email: String): AuthenticationResponse {


        val user = userDetailService.loadUserByUsername(email)
        val accessToken = tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return AuthenticationResponse(
            accessToken = accessToken
        )
    }
}
