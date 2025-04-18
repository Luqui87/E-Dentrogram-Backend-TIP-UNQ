package com.example.E_Dentogram.controller

import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
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
    private lateinit var patientRepository : PatientRepository

    @Autowired
    private lateinit var dentistRepository : DentistRepository

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
        dentistRepository.deleteAll()
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
            .andExpect(status().reason("This patient does not exist"))
    }

    @Test
    fun `should get the specific patient`(){

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
    fun `should get one simple patient`(){
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
        patientRepository.save(patient)

        mockMVC.perform(get("/allSimplePatients"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize<Array<Any>>(1)))
            .andExpect(jsonPath("$[0].medicalRecord").value(123))
            .andExpect(jsonPath("$[0].dni").value(42421645))
            .andExpect(jsonPath("$[0].name").value("Lucas Alvarez"))
            .andExpect(jsonPath("$[0].address").value("Bragado 1413"))
            .andExpect(jsonPath("$[0].birthdate").value("2000-10-12"))
            .andExpect(jsonPath("$[0].telephone").value(1153276406))
            .andExpect(jsonPath("$[0].email").value("lucas@mail.com"))
    }


    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @AutoConfigureMockMvc
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
    inner class MockedRepositoryTest {

        @Autowired
        private lateinit var mockMVC: MockMvc

        @MockitoBean
        private lateinit var patientRepository: PatientRepository


    }

}