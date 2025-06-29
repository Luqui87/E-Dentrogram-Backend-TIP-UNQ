package com.example.E_Dentogram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class EDentogramApplication

fun main(args: Array<String>) {
	runApplication<EDentogramApplication>(*args)
}
