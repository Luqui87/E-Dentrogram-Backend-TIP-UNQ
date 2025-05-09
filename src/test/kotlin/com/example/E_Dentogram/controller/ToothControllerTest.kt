package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.model.*
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.repository.ToothRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType
import java.time.LocalDate

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ToothControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var toothRepository: ToothRepository

    @Autowired
    private lateinit var dentistRepository: DentistRepository

    companion object{
        @Container
        private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }

    private fun getAccessToken(): String {
        val registerDTO = mapOf(
            "username" to "User2",
            "password" to "password2"
        )

        val result = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated).andReturn()

        val response = result.response.contentAsString
        val tokenResponse = jacksonObjectMapper().readValue(response, AuthenticationResponse::class.java)

        return tokenResponse.accessToken
    }

    fun createPatient(): Patient {
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .password("password1")
            .patients(mutableListOf())
            .build()

        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(1153276406)
            .email("lucas@mail.com")
            .dentist(dentist)
            .build()

        dentistRepository.save(dentist)
        val savePatient = patientRepository.save(patient)

        return savePatient
    }

    @BeforeEach
    fun setup() {
        dentistRepository.deleteAll()
        patientRepository.deleteAll()
        toothRepository.deleteAll()
    }

    @Test
    fun `should not find the patient`() {
        val token = getAccessToken()

        mockMvc.perform(get("/tooth/123")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)
            .andExpect(status().reason("This patient does not exist"))
    }

    @Test
    fun `teeth should be an empty list`() {
        createPatient()
        val token = this.getAccessToken()

        mockMvc.perform(get("/tooth/123")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(0)))
    }

    @Test
    fun `should get one tooth`() {
        val patient = this.createPatient()
        val token = this.getAccessToken()

        val tooth = Tooth.ToothBuilder()
            .patient(patient)
            .number(1)
            .up(PartialToothState.HEALTHY)
            .left(PartialToothState.HEALTHY)
            .center(PartialToothState.HEALTHY)
            .right(PartialToothState.HEALTHY)
            .down(PartialToothState.HEALTHY)
            .build()

        toothRepository.save(tooth)

        mockMvc.perform(get("/tooth/123")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$[0].number").value(1))
    }

    @Test
    fun `should not update when no body is present`() {
        val token = this.getAccessToken()

        mockMvc.perform(put("/update/tooth/123")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should not update when tooth state is wrong`() {
        this.createPatient()
        val token = this.getAccessToken()

        val body =
            mapOf(
                "number" to "1",
                "up" to "STATE",  // Este es un valor incorrecto
                "right" to "HEALTHY",
                "down" to "HEALTHY",
                "left" to "HEALTHY",
                "center" to "HEALTHY"
            )

        mockMvc.perform(put("/update/tooth/123")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andExpect(status().isBadRequest)
            .andExpect(status().reason("Invalid data provided for teeth update"))
    }

    @Test
    fun `should update teeth`() {
        this.createPatient()
        val token = this.getAccessToken()

        val body =
            mapOf(
                "number" to "1",
                "up" to "HEALTHY",
                "right" to "HEALTHY",
                "down" to "HEALTHY",
                "left" to "HEALTHY",
                "center" to "HEALTHY"
            )

        mockMvc.perform(put("/update/tooth/123")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.number").value(1))
            .andExpect(jsonPath("$.up").value("HEALTHY"))
    }

    @Test
    fun `all teeth should be an empty list`() {
        val token = getAccessToken()

        mockMvc.perform(get("/allTooth")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(0)))
    }
}
