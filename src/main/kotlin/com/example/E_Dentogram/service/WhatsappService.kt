package com.example.E_Dentogram.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

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
}
