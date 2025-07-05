package com.example.E_Dentogram.service

import com.example.E_Dentogram.repository.DentistRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.io.File
import java.nio.file.Files

@Service
@Transactional
class WhatsappService {

    @Autowired
    private lateinit var dentistRepository: DentistRepository

    @Autowired
    private lateinit var tokenService: TokenService

    private val webClient = WebClient.builder()
        .baseUrl("http://localhost:3001") //.baseUrl("http://whatsapp:3001") // nombre del servicio en Docker
        .build()

    fun sendMsg(number: String, message: String): String {
        val requestBody = mapOf("number" to number, "message" to message)

        return webClient.post()
            .uri("/send")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorMap { ex ->
                RuntimeException("Error al enviar mensaje", ex)
            }

            .block() ?: throw RuntimeException("Respuesta vacía del microservicio")
    }

    fun getQrCode(): String {
        return webClient.get()
            .uri("/qr")
            .retrieve()
            .onStatus({ status -> status.value() == 404 }) {
                Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "QR no disponible"))
            }
            .onStatus({ status -> status.isError }) { response ->
                Mono.error(RuntimeException("Error al obtener el QR: ${response.statusCode()}"))
            }
            .bodyToMono(String::class.java)
            .block() ?: throw RuntimeException("No se recibió el QR")
    }

    fun sendMsgWithFiles(
        number: String,
        message: String,
        files: List<File>,
        docs: List<String>?,
        token: String
    ): String {
        val multipartBuilder = MultipartBodyBuilder()



        multipartBuilder.part("number", number)
        multipartBuilder.part("message", message)

        val username = tokenService.extractUsername(token.removePrefix("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val dentist = dentistRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist")

        files.forEach { file ->
            multipartBuilder
                .part("files", file.readBytes())
                .filename(file.name)
                .header("Content-Type", Files.probeContentType(file.toPath()) ?: "application/octet-stream")
        }

        val selectedDocs = if (!docs.isNullOrEmpty()) {
            dentist.documents?.filter { it.fileName in docs } ?: emptyList()
        } else {
            emptyList()
        }

        selectedDocs.forEach { doc ->
            val resource = object : ByteArrayResource(doc.data) {
                override fun getFilename(): String = doc.fileName
            }

            multipartBuilder
                .part("files", resource)
                .header("Content-Type", "application/octet-stream")
                .filename(doc.fileName)

            multipartBuilder.part("docs", doc.fileName)
        }

        println("Docs recibidos: $docs")
        println("Selected docs encontrados: ${selectedDocs.map { it.fileName }}")

        return webClient.post()
            .uri("/send-multiple-files")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipartBuilder.build()))
            .retrieve()
            .bodyToMono(String::class.java)
            .block() ?: throw RuntimeException("Respuesta vacía del microservicio")
    }

}
