package com.example.E_Dentogram.service

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

    fun sendMsgWithFiles(number: String, message: String, files: List<File>): String {
        val multipartBuilder = MultipartBodyBuilder()
        multipartBuilder.part("number", number)
        multipartBuilder.part("message", message)

        files.forEach { file ->
            multipartBuilder
                .part("files", file.readBytes())
                .filename(file.name)
                .header("Content-Type", Files.probeContentType(file.toPath()) ?: "application/octet-stream")
        }

        return webClient.post()
            .uri("/send-multiple-files")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipartBuilder.build()))
            .retrieve()
            .bodyToMono(String::class.java)
            .block() ?: throw RuntimeException("Respuesta vacía del microservicio")
    }

}
