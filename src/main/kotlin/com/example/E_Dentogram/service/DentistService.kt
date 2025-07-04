package com.example.E_Dentogram.service

import com.example.E_Dentogram.config.JwtProperties
import com.example.E_Dentogram.dto.*
import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Document
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Generated
@Service
@Transactional
class DentistService(
    private val userDetailService: CustomUserDetailService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val googleIdTokenVerifier: GoogleIdTokenVerifier
) {

    @Autowired
    private lateinit var encoder: PasswordEncoder

    @Value("\${google.client.clientId}")
    private val clientId: String? = null

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
                name = dentist.name!!,
                email = dentist.email!!)
        }
        return dentistDTOs
    }

    @Transactional(readOnly=true)
    fun getDentist(token: String): DentistDTO {
        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

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
                    .historial(mutableListOf())
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
            .name(dentistDTO.name)
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

    fun signUpGoogle(googleTokenDTO: GoogleTokenDTO): AuthenticationResponse? {

        val idToken = googleIdTokenVerifier.verify(googleTokenDTO.token)
        if (idToken != null) {
            val payload = idToken.payload
            val email = payload.email
            val name = payload["name"] as? String ?: "Unknown"

            if (dentistRepository.existsDentistByEmail(email)) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user already exists")
            }

            val dentist = Dentist.DentistBuilder()
                .username(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .password(UUID.randomUUID().toString())
                .build()

             dentistRepository.save(dentist)

            val userDetails = userDetailService.loadUserByUsername(dentist.email!!)
            val accessToken = tokenService.generate(
                userDetails,
                Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
            )

            return AuthenticationResponse(accessToken = accessToken)
        }
        else{
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
    }

    fun updateTags(tags: List<String>, token: String): DentistDTO? {
        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val dentist = dentistRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist")

        dentist.tags = tags

        dentistRepository.save(dentist)

        return DentistDTO.fromModel(dentist)
    }

    @Transactional(readOnly = true)
    fun getDentistPatient(token: String, pageNumber: Int): PatientPaginationDTO {
        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val pageSize = 10
        val pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending())
        val patientPage = patientRepository.findByDentistUsername(username, pageRequest)

        return PatientPaginationDTO.fromModel(patientPage)
    }


    fun getDentistPatientQuery(token: String, pageNumber: Int, query: String): PatientPaginationDTO? {
        if (query.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be blank")
        }

        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val pageSize = 10
        val pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending())
        val patientPage = patientRepository.findByDentistUsernameAndNameContainingIgnoreCase(username, query, pageRequest)

        return PatientPaginationDTO.fromModel(patientPage)
    }

    fun updateDocuents(files : List<MultipartFile>, token: String) : DentistDTO {
        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val dentist = dentistRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist")

        val documents = files.map { file ->
            Document.DocumentBuilder()
                .fileName(file.originalFilename ?: "unnamed.pdf")
                .data(file.bytes)
                .dentist(dentist)
                .build()
        }.toMutableList()

        dentist.documents!!.addAll(documents)

        dentistRepository.save(dentist)


        return DentistDTO.fromModel(dentist)
    }
}