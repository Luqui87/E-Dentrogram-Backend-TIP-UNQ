package com.example.E_Dentogram.service

import com.example.E_Dentogram.repository.TurnRepository
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory

@Service
class ReminderService(
    private val turnRepository: TurnRepository,
    private val whatsappService: WhatsappService,
    private val logger: Logger = LoggerFactory.getLogger(ReminderService::class.java)
) {

    @Scheduled(cron = "0 0 10 * * *", zone = "America/Argentina/Buenos_Aires")
    fun sendReminders() {
        sendTomorrowReminders()
        deletePastTurns()
    }


    private fun sendTomorrowReminders() {
        val tomorrow = LocalDate.now().plusDays(1)
        val startOfDay = tomorrow.atStartOfDay()
        val endOfDay = tomorrow.atTime(LocalTime.MAX)

        val turnsTomorrow = turnRepository.findByDateBetween(startOfDay, endOfDay)

        for (turn in turnsTomorrow) {
            val patient = turn.patient!!
            val phone = patient.telephone ?: continue

            val date = turn.date!!.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val time = turn.date!!.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

            val message = "Recordatorio: tienes un turno con el dentista el $date a las $time."

            try {
                whatsappService.sendMsg(phone.toString(), message)
                logger.info("------------------------------ Mensaje enviado a $phone")
            } catch (e: Exception) {
                logger.info("----------------------- Error enviando mensaje a $phone: ${e.message}")
            }
        }
    }

    private fun deletePastTurns() {
        val now = LocalDateTime.now()
        val oldTurns = turnRepository.findByDateBefore(now)

        if (oldTurns.isNotEmpty()) {
            turnRepository.deleteAll(oldTurns)
            logger.info(" ${oldTurns.size} turnos pasados eliminados")
        }
    }

}
