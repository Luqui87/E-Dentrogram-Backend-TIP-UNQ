package com.example.E_Dentogram.controller

import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.model.ToothState
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.repository.ThoothRepository
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
    private lateinit var toothRepository: ThoothRepository

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

    fun createPatient(): Patient{
        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(1153276406)
            .email("lucas@mail.com")
            .build()

        val savePatient = patientRepository.save(patient)

        return savePatient
    }

    @BeforeEach
    fun setup() {
        patientRepository.deleteAll()
        toothRepository.deleteAll()
    }

    @Test
    fun `should not find the patient`(){

        mockMvc.perform(get("/tooth/123"))
            .andExpect(status().isNotFound)
            .andExpect(status().reason("This patient does not exist"))

    }

     @Test
     fun `teeth should be an empty list`(){
         createPatient()

         mockMvc.perform(get("/tooth/123"))
             .andExpect(status().isOk)
             .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(0)))
     }

    @Test
    fun `should get one tooth`(){
        val patient = createPatient()

        val tooth = Tooth.ToothBuilder()
            .patient(patient)
            .number(1)
            .up(ToothState.HEALTHY)
            .left(ToothState.HEALTHY)
            .center(ToothState.HEALTHY)
            .right(ToothState.HEALTHY)
            .down(ToothState.HEALTHY)
            .build()

        toothRepository.save(tooth)


        mockMvc.perform(get("/tooth/123"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$[0].number").value(1))

    }


    @Test
    fun `should not update when no body is present`(){
        mockMvc.perform(put("/update/tooth/123"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should update teeth`(){
        createPatient()

        val body = listOf(
            mapOf(
                "number" to "1",
                "up" to "HEALTHY",
                "right" to "HEALTHY",
                "down" to "HEALTHY",
                "left" to "HEALTHY",
                "center" to "HEALTHY"
            )
        )

        mockMvc.perform(put("/update/tooth/123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(body)))

            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$[0].number").value(1))
            .andExpect(jsonPath("$[0].up").value("HEALTHY"))


    }


}