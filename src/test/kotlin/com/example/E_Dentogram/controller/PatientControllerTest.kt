package com.example.E_Dentogram.controller

import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.PatientRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientControllerTest {


    @Autowired
    private lateinit var mockMVC : MockMvc

    @Autowired
    private lateinit var patientRepository: PatientRepository

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

    @BeforeEach
    fun setup() {
        patientRepository.deleteAll()
    }

    @Test
    fun `should get no patients `(){
        mockMVC.perform(get("/allPatients"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(0)))
    }

    @Test
    fun `should get one patient`(){
        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(1153276406)
            .email("lucas@mail.com")
            .build()

        patientRepository.save(patient)

        mockMVC.perform(get("/allPatients"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$[0].medicalRecord").value(123))
            .andExpect(jsonPath("$[0].name").value("Lucas Alvarez"))
            .andExpect(jsonPath("$[0].dni").value(42421645))
    }

    @Test
    fun `should not get specific patient`(){

        mockMVC.perform(get("/patient/123"))
            .andDo(print())
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should get the specific patient`(){

        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(1153276406)
            .email("lucas@mail.com")
            .build()

        patientRepository.save(patient)

        mockMVC.perform(get("/patient/123"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.medicalRecord").value(123))
            .andExpect(jsonPath("$.dni").value(42421645))
            .andExpect(jsonPath("$.name").value("Lucas Alvarez"))
            .andExpect(jsonPath("$.address").value("Bragado 1413"))
            .andExpect(jsonPath("$.birthdate").value("2000-10-12"))
            .andExpect(jsonPath("$.telephone").value(1153276406))
            .andExpect(jsonPath("$.email").value("lucas@mail.com"))
            .andExpect(jsonPath("$.teeth", IsCollectionWithSize.hasSize<Array<Any>>(0)))

    }

    @Test
    fun `should create a patient`(){
        var body = mapOf(
            "medicalRecord" to "123",
            "dni" to "42421645",
            "name" to "Lucas Alvarez",
            "address" to "Bragado 1413",
            "birthdate" to "2000-10-12",
            "telephone" to "1153276406",
            "email" to "lucas@mail.com"
        )

        mockMVC.perform(post("/patient/123)")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.medicalRecord").value(123))
            .andExpect(jsonPath("$.dni").value(42421645))
            .andExpect(jsonPath("$.name").value("Lucas Alvarez"))
            .andExpect(jsonPath("$.address").value("Bragado 1413"))
            .andExpect(jsonPath("$.birthdate").value("2000-10-12"))
            .andExpect(jsonPath("$.telephone").value("1153276406"))
            .andExpect(jsonPath("$.email").value("lucas@mail.com"))
    }

}