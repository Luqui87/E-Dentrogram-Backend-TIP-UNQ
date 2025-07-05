package com.example.E_Dentogram.controller

import com.example.E_Dentogram.dto.AuthenticationResponse
import com.example.E_Dentogram.dto.WhatsappRequest
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.service.WhatsappService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockPart
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WhatsappControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var whatsappService: WhatsappService



    private val objectMapper = ObjectMapper()

    @Autowired
    lateinit var dentistRepository: DentistRepository

    @BeforeEach
    fun cleanDb() {
        dentistRepository.deleteAll()
    }

    companion object {
        @Container
        private val postgres = PostgreSQLContainer<Nothing>("postgres:latest").apply {
            withDatabaseName("testdb")
            withUsername("user")
            withPassword("password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    @WithMockUser(username = "testUser", roles = ["USER"])
    fun `debería enviar mensaje correctamente`() {
        val request = WhatsappRequest("5491123456789", "Hola desde test!")
        val requestJson = objectMapper.writeValueAsString(request)

        Mockito.`when`(whatsappService.sendMsg(request.number, request.message))
            .thenReturn("Mensaje enviado")

        mockMvc.perform(
            post("/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Mensaje enviado"))
    }

    @Test
    @WithMockUser(username = "testUser", roles = ["USER"])
    fun `debería obtener QR correctamente`() {
        Mockito.`when`(whatsappService.getQrCode()).thenReturn("QR-BASE64")

        mockMvc.perform(get("/qr"))
            .andExpect(status().isOk)
            .andExpect(content().string("QR-BASE64"))
    }

    @Test
    fun `debería enviar mensaje con archivos`() {

        val registerDTO = mapOf(
            "username" to "User1010",
            "name" to "User1010_name",
            "password" to "password1010",
            "email" to "User1010@gmail.com"
        )

        val result = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jacksonObjectMapper().writeValueAsString(registerDTO)))
            .andExpect(status().isCreated).andReturn()

        val response = result.response.contentAsString
        val tokenResponse = jacksonObjectMapper().readValue(response, AuthenticationResponse::class.java).accessToken

        val mockFile = MockMultipartFile(
            "files", "archivo.txt", MediaType.TEXT_PLAIN_VALUE, "contenido".toByteArray()
        )

        Mockito.`when`(
            whatsappService.sendMsgWithFiles(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.anyString()
            )
        ).thenReturn("Archivos enviados")

        mockMvc.perform(
            multipart("/send-multiple-files")
                .file(mockFile)
                .part(MockPart("number", "5491123456789".toByteArray()))
                .part(MockPart("message", "Adjunto".toByteArray()))
                .param("doc", "doc1.pdf")
                .header("Authorization", "Bearer $tokenResponse")
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Archivos enviados"))
    }
}
