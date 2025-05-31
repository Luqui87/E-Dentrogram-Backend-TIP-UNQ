package com.example.E_Dentogram.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient

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
                println("Error real: ${ex.message}")
                RuntimeException("Error al enviar mensaje", ex)
            }

            .block() ?: throw RuntimeException("Respuesta vacía del microservicio")
    }
}
