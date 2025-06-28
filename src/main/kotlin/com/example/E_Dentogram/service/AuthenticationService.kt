package com.example.E_Dentogram.service

import com.example.E_Dentogram.config.JwtProperties
import com.example.E_Dentogram.dto.AuthenticationRequest
import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.dto.GoogleTokenDTO
import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.repository.DentistRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import org.springframework.security.core.userdetails.UsernameNotFoundException


@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailService: CustomUserDetailService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val googleIdTokenVerifier: GoogleIdTokenVerifier,
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

    fun authenticationGoogle(googleToken: GoogleTokenDTO): ResponseEntity<AuthenticationResponse> {

        return try {
            val idToken = googleIdTokenVerifier.verify(googleToken.token)
            if (idToken != null){
                val payload = idToken.payload
                val email = payload.email

                var responseStatus = HttpStatus.OK

                val user = try {
                    userDetailService.loadUserByUsername(email)


                } catch (e: UsernameNotFoundException) {
                    val name = payload["name"] as? String ?: "Unknown"

                    val dentist = Dentist.DentistBuilder()
                        .username(UUID.randomUUID().toString())
                        .name(name)
                        .email(email)
                        .password(UUID.randomUUID().toString())
                        .build()

                    dentistRepository.save(dentist)

                    responseStatus = HttpStatus.CREATED

                    userDetailService.loadUserByUsername(dentist.email!!)

                }

                val accessToken = tokenService.generate(
                    userDetails = user,
                    expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
                )

                val response = AuthenticationResponse(accessToken = accessToken)

                ResponseEntity.status(responseStatus).body(response)
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
