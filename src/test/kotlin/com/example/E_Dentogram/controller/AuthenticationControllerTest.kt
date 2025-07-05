package com.example.E_Dentogram.controller

import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.repository.DentistRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    @MockitoBean
    private lateinit var googleIdTokenVerifier: GoogleIdTokenVerifier

    @Autowired
    private lateinit var dentistRepository: DentistRepository

    companion object {
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

    @BeforeEach
    fun setup() {
        dentistRepository.deleteAll()
    }

    fun createDentist(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        dentistRepository.save(dentist)
    }

    @Test
    fun `should not authenticated invalid google token`() {
        val body = mapOf(
            "token" to "invalid-token"
        )

        `when`(googleIdTokenVerifier.verify(anyString()))
            .thenReturn(null as GoogleIdToken?)

        mockMvc.perform(post("/login/google")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should not authenticated invalid token`(){
        this.createDentist()

        val body = mapOf(
            "username" to "invalid-user",
            "password" to "password"
        )

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isForbidden)


    }

    @Test
    fun `should authenticated valid google token`() {
        this.createDentist()

        val googleToken : GoogleIdToken = Mockito.mock(GoogleIdToken::class.java)
        val mockPayload: GoogleIdToken.Payload = GoogleIdToken.Payload()
            .setEmail("User1@gmail.com")
            .setEmailVerified(true)
            .setSubject("User1_name")

        val body = mapOf(
            "token" to "eyJhbGciOiJSUzI1NiIsImtpZCI6Ik5FWTBOekUyUlRRd05UYzVOVGcyTkRrNU1qQXdRVFUwTmtFMk1rWkNORE14TWpnNE5qYzVNZyJ9.eyJuaWNrbmFtZSI6ImpvaG5kb2UiLCJuYW1lIjoiSm9obiBEb2UiLCJwaWN0dXJlIjoiaHR0cHM6Ly9hdmF0YXJzLmF1dGgwLmNvbS9hdmF0YXIuanBnIiwidXBkIjoxNjk0ODU4ODAwLCJzdWIiOiJhdXRoMHw2NTg4NDMyYjFhODJmODAxZGRmYjU1Y2IiLCJlbWFpbCI6ImplbWlAeGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczovL2FjY291bnQteHl6LmF1dGgwLmNvbS8iLCJhdWQiOiJodHRwczovL2FwaS54eXouY29tIiwiaWF0IjoxNzE3OTgwNzAwLCJleHAiOjE3MTgwNjYxMDB9.NK4z_KX0A6sZcIs8WT8RQoz5vQmpaAwqxEas5P0zY_OmC0O_UMxjHzYcWx94xY8SpC4sHE6-5Gl8rkKOos7xxdfNwe_pCFFtXGFOdJrD6fqOjROV_1TkFt8G8mavAdoO7shCLoCF2OBkWkTrqj7kTqGR0FoUdK70mXqfHdIBsPME"
        )

        `when`(googleToken.payload)
            .thenReturn(mockPayload)

        `when`(googleIdTokenVerifier.verify(anyString()))
            .thenReturn(googleToken)

        mockMvc.perform(post("/login/google")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)

    }

    @Test
    fun `should authenticated valid token`() {
        this.createDentist()

        val body = mapOf(
            "username" to "User2",
            "password" to "password2"
        )

        val registerDTO = mapOf(
            "username" to "User2",
            "name" to "User2_name",
            "password" to "password2",
            "email" to "User2@gmail.com"
        )

        mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated).andReturn()

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper.writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)


    }



}