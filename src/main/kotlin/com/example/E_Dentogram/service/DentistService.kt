package com.example.E_Dentogram.service

import com.example.E_Dentogram.config.JwtProperties
import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.dto.DentistDTO
import com.example.E_Dentogram.dto.DentistSimpleDTO
import com.example.E_Dentogram.dto.PatientDTO
import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Generated
@Service
@Transactional
class DentistService(
    private val userDetailService: CustomUserDetailService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
) {

    @Autowired
    private lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var dentistRepository : DentistRepository
    @Autowired
    lateinit var patientRepository : PatientRepository


    @Transactional(readOnly=true)
    fun allDentist(): List<DentistSimpleDTO>? {
        val dentists = dentistRepository.findAll()

        val dentistDTOs = dentists.map {
            dentist -> DentistSimpleDTO(
                username = dentist.username!!,
                password = dentist.password!!,
                email = dentist.email!!)
        }
        return dentistDTOs
    }

    @Transactional(readOnly=true)
    fun getDentist(token: String): DentistDTO {
        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED,"Hola")

        val dentist = dentistRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist")

        return DentistDTO.fromModel(dentist)
    }

    fun removePatient(dentistId: Long,patientMedicalRecord: Int) {
        val dentist = dentistRepository.findById(dentistId).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }

        try {
            dentist.removePatient(patientMedicalRecord)

            dentistRepository.save(dentist)
        }catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to save changes: ${e.message}")
        }

    }

    fun addPatient(dentistId: Long, patientDTO: PatientDTO): DentistDTO {
        val dentist = dentistRepository.findById(dentistId).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }

        var patient = patientRepository.findById(patientDTO.medicalRecord).orElse(null)

        if (patient == null) {
            val newPatient = try {
                Patient.PatientBuilder()
                    .medicalRecord(patientDTO.medicalRecord)
                    .dni(patientDTO.dni)
                    .name( patientDTO.name)
                    .address(patientDTO.address)
                    .birthdate(patientDTO.birthdate)
                    .telephone(patientDTO.telephone)
                    .email(patientDTO.email)
                    .teeth(mutableListOf())
                    .build()
            } catch (e: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data provided for register patient : ${e.message}", e)
            }
            patient = newPatient
        }

        dentist.addPatient(patient)

        try {
            dentistRepository.save(dentist)
        }catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to save changes: ${e.message}")
        }

        return DentistDTO.fromModel(dentist)

    }

    fun signUp(dentistDTO: DentistSimpleDTO): AuthenticationResponse {
        if (dentistRepository.existsDentistByUsername(dentistDTO.username)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user already exists")
        }

        val dentist = Dentist.DentistBuilder()
            .username(dentistDTO.username)
            .password(encoder.encode(dentistDTO.password))
            .email(dentistDTO.email)
            .build()

        dentistRepository.save(dentist)

        val userDetails = userDetailService.loadUserByUsername(dentist.username!!)
        val token = tokenService.generate(
            userDetails,
            Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )

        return AuthenticationResponse(accessToken = token)
    }

}