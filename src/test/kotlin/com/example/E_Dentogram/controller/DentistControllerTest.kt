package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
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
            "password" to "password2000"
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
            "password" to dentist.password
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

    /* VER
    @Test
    fun `should not get specific dentist`(){

        val accessToken = this.getToken()

        mockMVC.perform(get("/dentist/123")
                .header("Authorization", "Bearer $accessToken")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound) // mirar el orden en el service
            .andExpect(status().reason("This dentist does not exist"))
    }
    */

    @Test
    fun `should get dentist with the username`(){

        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .password("password1")
            .patients(mutableListOf())
            .build()

        val accessToken = this.getTokenForUser(dentist)

        val registeredDentist = dentistRepository.findByUsername("User1")!!

        mockMVC.perform(get("/dentist/${registeredDentist.id}")
                .header("Authorization", "Bearer $accessToken")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dentistID").value(registeredDentist.id))
            .andExpect(jsonPath("$.username").value("User1"))
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(0)))
    }



    @Test
    fun `should add patient to the dentist`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .password("password1")
            .patients(mutableListOf())
            .build()

        val otherDentist = Dentist.DentistBuilder()
            .username("User2")
            .password("password2")
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

        mockMVC.perform(put("/dentist/add/${registeredDentist.id}")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dentistID").value(registeredDentist.id))
            .andExpect(jsonPath("$.username").value("User1"))
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(1)))
    }

    @Test
    fun `should create and add patient to the dentist`(){
        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .password("password1")
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

        mockMVC.perform(put("/dentist/add/${registeredDentist.id}")
            .header("Authorization", "Bearer $accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.dentistID").value(registeredDentist.id))
            .andExpect(jsonPath("$.username").value("User1"))
            .andExpect(jsonPath("$.patients", IsCollectionWithSize.hasSize<Array<Any>>(1)))
    }


    @Test
    fun `should remove patient from the dentist`() {

        val dentist = Dentist.DentistBuilder()
            .username("User1")
            .password("password1")
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
            .password("password1")
            .patients(mutableListOf())
            .build()

        val otherDentist = Dentist.DentistBuilder()
            .username("User2")
            .password("password2")
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

}