package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
class DentistControllerTest{

    @Autowired
    private lateinit var mockMVC : MockMvc

    @Autowired
    private lateinit var dentistRepository : DentistRepository

    @Autowired
    private lateinit var patientRepository : PatientRepository

    @MockitoBean
    private lateinit var googleIdTokenVerifier: GoogleIdTokenVerifier

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

    private fun getToken(): String {
        val registerDTO = mapOf(
            "username" to "User2000",
            "name" to "User2000_name",
            "password" to "password2000",
            "email" to "User2000@gmail.com"
        )

        val result = mockMVC.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated).andReturn()

        val response = result.response.contentAsString
        val tokenResponse = jacksonObjectMapper().readValue(response, AuthenticationResponse::class.java)
        return tokenResponse.accessToken
    }

    private fun getTokenForUser(dentist: Dentist): String {
        val registerDTO = mapOf(
            "username" to dentist.username,
            "name" to dentist.name,
            "password" to dentist.password,
            "email" to dentist.email
        )
        val result = mockMVC.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated).andReturn()

        val response = result.response.contentAsString
        val tokenResponse = jacksonObjectMapper().readValue(response, AuthenticationResponse::class.java)
        return tokenResponse.accessToken
    }

    @BeforeEach
    fun setup() {
        dentistRepository.deleteAll()
        patientRepository.deleteAll()
    }

    @Test
    fun `should registrate a dentist `(){
        val registerDTO = mapOf(
            "username" to "User",
            "name" to "Natalia Natalia",
            "password" to "password",
            "email" to "User@gmail.com"
        )

         mockMVC.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated)
    }

    @Test
    fun `should not create the same dentist `(){
        val registerDTO = mapOf(
            "username" to "User2000",
            "name" to "User2000_name",
            "password" to "password2000",
            "email" to "User2000@gmail.com"
        )

        mockMVC.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated)

        mockMVC.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should not create a dentist with a invalid google token`() {
        val body = mapOf(
            "token" to "invalid-token"
        )

        `when`(googleIdTokenVerifier.verify(anyString()))
            .thenReturn(null as GoogleIdToken?)

        mockMVC.perform(post("/register/google")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should register the dentist with a valid google token`() {

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

        mockMVC.perform(post("/register/google")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)

    }

    @Test
    fun `should not register a dentist with valid the same email`() {

        this.getToken()

        val googleToken : GoogleIdToken = Mockito.mock(GoogleIdToken::class.java)
        val mockPayload: GoogleIdToken.Payload = GoogleIdToken.Payload()
            .setEmail("User2000@gmail.com")
            .setEmailVerified(true)
            .setSubject("User1_name")

        val body = mapOf(
            "token" to "eyJhbGciOiJSUzI1NiIsImtpZCI6Ik5FWTBOekUyUlRRd05UYzVOVGcyTkRrNU1qQXdRVFUwTmtFMk1rWkNORE14TWpnNE5qYzVNZyJ9.eyJuaWNrbmFtZSI6ImpvaG5kb2UiLCJuYW1lIjoiSm9obiBEb2UiLCJwaWN0dXJlIjoiaHR0cHM6Ly9hdmF0YXJzLmF1dGgwLmNvbS9hdmF0YXIuanBnIiwidXBkIjoxNjk0ODU4ODAwLCJzdWIiOiJhdXRoMHw2NTg4NDMyYjFhODJmODAxZGRmYjU1Y2IiLCJlbWFpbCI6ImplbWlAeGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczovL2FjY291bnQteHl6LmF1dGgwLmNvbS8iLCJhdWQiOiJodHRwczovL2FwaS54eXouY29tIiwiaWF0IjoxNzE3OTgwNzAwLCJleHAiOjE3MTgwNjYxMDB9.NK4z_KX0A6sZcIs8WT8RQoz5vQmpaAwqxEas5P0zY_OmC0O_UMxjHzYcWx94xY8SpC4sHE6-5Gl8rkKOos7xxdfNwe_pCFFtXGFOdJrD6fqOjROV_1TkFt8G8mavAdoO7shCLoCF2OBkWkTrqj7kTqGR0FoUdK70mXqfHdIBsPME"
        )

        `when`(googleToken.payload)
            .thenReturn(mockPayload)

        `when`(googleIdTokenVerifier.verify(anyString()))
            .thenReturn(googleToken)

        mockMVC.perform(post("/register/google")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isUnauthorized)

    }

    @Test
    fun `should get a dentist `(){

        val accessToken = this.getToken()

        mockMVC.perform(
            get("/allDentist")
                .header("Authorization", "Bearer $accessToken")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(1)))
    }

    @Test
    fun `should not get a dentist without authorization`(){

        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        this.getTokenForUser(dentist)

         dentistRepository.findByUsername("User1")!!

        mockMVC.perform(get("/dentist/user"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isForbidden)

    }

    @Test
    fun `should get dentist with the username`(){

        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(get("/dentist/user")
                .header("Authorization", "Bearer $accessToken")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dentistID").value(registeredDentist.id))
            .andExpect(jsonPath("$.username").value("User1"))
            .andExpect(jsonPath("$.name").value("User1_name"))
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(0)))
    }



    @Test
    fun `should add patient to the dentist`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        val otherDentist = Dentist.DentistBuilder()
            .username("User2")
            .name("User2_name")
            .password("password2")
            .email("User2@gmail.com")
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
            .dentist(otherDentist)
            .build()

        otherDentist.patients!!.add(patient)

        val body = mapOf(
            "medicalRecord" to "123",
            "dni" to "42421645",
            "name" to "Lucas Alvarez",
            "address" to "Bragado 1413",
            "birthdate" to "2000-10-12",
            "telephone" to "1153276406",
            "email" to "lucas@mail.com"
        )

        val accessToken = this.getTokenForUser(dentist)

        dentistRepository.save(otherDentist)
        patientRepository.save(patient)

        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(post("/dentist/add/${registeredDentist.id}")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dentistID").value(registeredDentist.id))
            .andExpect(jsonPath("$.username").value("User1"))
            .andExpect(jsonPath("$.name").value("User1_name"))
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(1)))
    }

    @Test
    fun `should not create and add a patient with wrong birthdate to the dentist`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()


        val body = mapOf(
            "medicalRecord" to "123",
            "dni" to "42421645",
            "name" to "Lucas Alvarez",
            "address" to "Bragado 1413",
            "birthdate" to "2030-10-12",
            "telephone" to "1153276406",
            "email" to "lucas@mail.com"
        )

        val accessToken = this.getTokenForUser(dentist)
        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(post("/dentist/add/${registeredDentist.id}")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest)

    }

    @Test
    fun `should create and add patient to the dentist`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()


        val body = mapOf(
            "medicalRecord" to "123",
            "dni" to "42421645",
            "name" to "Lucas Alvarez",
            "address" to "Bragado 1413",
            "birthdate" to "2000-10-12",
            "telephone" to "1153276406",
            "email" to "lucas@mail.com"
        )

        val accessToken = this.getTokenForUser(dentist)
        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(post("/dentist/add/${registeredDentist.id}")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dentistID").value(registeredDentist.id))
            .andExpect(jsonPath("$.username").value("User1"))
            .andExpect(jsonPath("$.name").value("User1_name"))
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(1)))
    }


    @Test
    fun `should remove patient from the dentist`() {

        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)
        val registeredDentist = dentistRepository.findByUsername("User1")!!

        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(1153276406)
            .email("lucas@mail.com")
            .dentist(registeredDentist)
            .build()

        patientRepository.save(patient)

        mockMVC.perform(put("/dentist/Remove/${registeredDentist.id}/123")
                .header("Authorization", "Bearer $accessToken"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }


    @Test
    fun `should try remove patient to the dentist without the patient and do nothing`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        val otherDentist = Dentist.DentistBuilder()
            .username("User2")
            .name("User2_name")
            .password("password2")
            .email("User2@gmail.com")
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
            .dentist(otherDentist)
            .build()

        otherDentist.patients!!.add(patient)
        dentistRepository.save(otherDentist)
        patientRepository.save(patient)

        val accessToken = this.getTokenForUser(dentist)
        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(put("/dentist/Remove/${registeredDentist.id}/123")
                .header("Authorization", "Bearer $accessToken"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
    }


    @Test
    fun `should update dentist tags`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val body = listOf("Fluor", "Revisión", "Ortodoncia")

        mockMVC.perform(
            patch("/dentist/update/tags")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.tags", IsCollectionWithSize.hasSize<Array<Any>>(3)))
    }

    @Test
    fun `should get no patients`(){

        val dentist = Dentist.DentistBuilder()
            .username("User100")
            .name("User100_name")
            .password("password100")
            .email("User100@gmail.com")
            .patients(mutableListOf())
            .build()

        val patient1 = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(1153276406)
            .email("lucas@mail.com")
            .dentist(dentist)
            .build()

        val patient2 = Patient.PatientBuilder()
            .medicalRecord(124)
            .dni(42421612)
            .name("Nicolas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(1996, 10, 12))
            .telephone(1153276406)
            .email("nicolas@mail.com")
            .dentist(dentist)
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val page = 0
        mockMVC.perform(
            get("/dentist/patient//$page")
            .header("Authorization", "Bearer $accessToken"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(0)))
            .andExpect(jsonPath("$.total").value(0))
    }

    @Test
    fun `should get one patient`(){

        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()


        val body = mapOf(
            "medicalRecord" to "123",
            "dni" to "42421645",
            "name" to "Lucas Alvarez",
            "address" to "Bragado 1413",
            "birthdate" to "2000-10-12",
            "telephone" to "1153276406",
            "email" to "lucas@mail.com"
        )

        val accessToken = this.getTokenForUser(dentist)
        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(post("/dentist/add/${registeredDentist.id}")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))
            .andDo(MockMvcResultHandlers.print())
            .andReturn()

        val page = 0
        mockMVC.perform(
            get("/dentist/patient/$page")
                .header("Authorization", "Bearer $accessToken"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$.total").value(1))
    }

    @Test
    fun `should search and return one matching patient`() {
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val registeredDentist = dentistRepository.findByUsername("User1")!!

        val body = mapOf(
            "medicalRecord" to "123",
            "dni" to "42421645",
            "name" to "Lucas Alvarez",
            "address" to "Bragado 1413",
            "birthdate" to "2000-10-12",
            "telephone" to "1153276406",
            "email" to "lucas@mail.com"
        )

        mockMVC.perform(
            post("/dentist/add/${registeredDentist.id}")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper().writeValueAsString(body))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)

        val query = "Lucas"
        val page = 0

        mockMVC.perform(
            get("/dentist/patient/$query/$page")
                .header("Authorization", "Bearer $accessToken")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$.total").value(1))
    }


    @Test
    fun `should update dentist documents`() {
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .documents(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val file1 = MockMultipartFile(
            "documents", "doc1.pdf", "application/pdf", "Contenido del PDF 1".toByteArray()
        )

        val file2 = MockMultipartFile(
            "documents", "doc2.pdf", "application/pdf", "Contenido del PDF 2".toByteArray()
        )

        mockMVC.perform(
            multipart("/dentist/update/documents")
                .file(file1)
                .file(file2)
                .with { request ->
                    request.method = "PATCH" // multipart por defecto usa POST, lo forzamos a PATCH
                    request
                }
                .header("Authorization", "Bearer $accessToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.documents", IsCollectionWithSize.hasSize<Array<Any>>(2)))
    }

    @Test
    fun `should delete dentist documents`() {
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .name("User1_name")
            .password("password1")
            .email("User1@gmail.com")
            .patients(mutableListOf())
            .documents(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val file1 = MockMultipartFile(
            "documents", "doc1.pdf", "application/pdf", "Contenido del PDF 1".toByteArray()
        )



        mockMVC.perform(
            multipart("/dentist/update/documents")
                .file(file1)
                .with { request ->
                    request.method = "PATCH"
                    request
                }
                .header("Authorization", "Bearer $accessToken")
        )
            .andReturn()

        mockMVC.perform(
            delete("/dentist/delete/documents")
                .header("Authorization", "Bearer $accessToken")
                .param("doc", "doc1.pdf")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.documents", IsCollectionWithSize.hasSize<Array<Any>>(0)))
    }

    @Test
    fun `should return 404 when deleting non-existent document`() {
        val dentist = Dentist.DentistBuilder()
            .username("User404")
            .name("User404_name")
            .password("password404")
            .email("User404@gmail.com")
            .patients(mutableListOf())
            .documents(mutableListOf()) // No documentos
            .build()

        val accessToken = this.getTokenForUser(dentist)

        mockMVC.perform(
            delete("/dentist/delete/documents")
                .header("Authorization", "Bearer $accessToken")
                .param("doc", "nonexistent.pdf")
        )
            .andExpect(status().isNotFound)
    }

}