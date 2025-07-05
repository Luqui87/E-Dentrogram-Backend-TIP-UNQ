package com.example.E_Dentogram.service

import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.model.Patient
import com.example.E_Dentogram.model.Turn
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.repository.TurnRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.contains
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalDateTime

class ReminderServiceTest {

    private lateinit var turnRepository: TurnRepository
    private lateinit var whatsappService: WhatsappService
    private lateinit var reminderService: ReminderService


    @BeforeEach
    fun setup() {
        turnRepository = mock()
        whatsappService = mock()
        reminderService = ReminderService(turnRepository, whatsappService)
    }

    @Test
    fun `should send reminders to patients with tomorrow's appointments`() {
        val dentist = Dentist.DentistBuilder()
            .username("dentist2")
            .password("password2")
            .email("User2@gmail.com")
            .patients(mutableListOf())
            .build()

        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(5491122334455)
            .email("lucas@mail.com")
            .dentist(dentist)
            .build()

        val tomorrow = LocalDateTime.now().plusDays(1).withHour(10)
        val turn = Turn.TurnBuilder().patient(patient).date(tomorrow).build()

        whenever(turnRepository.findByDateBetween(any(), any())).thenReturn(listOf(turn))

        reminderService.sendReminders()


        verify(whatsappService).sendMsg(eq("5491122334455"), contains("Recordatorio"))
    }

    @Test
    fun `should delete past turns`() {
        val dentist = Dentist.DentistBuilder()
            .username("dentist2")
            .password("password2")
            .email("User2@gmail.com")
            .patients(mutableListOf())
            .build()


        val patient = Patient.PatientBuilder()
            .medicalRecord(123)
            .dni(42421645)
            .name("Lucas Alvarez")
            .address("Bragado 1413")
            .birthdate(LocalDate.of(2000, 10, 12))
            .telephone(5491122334455)
            .email("lucas@mail.com")
            .dentist(dentist)
            .build()

        val pastTurn = Turn.TurnBuilder().patient(patient).date(LocalDateTime.now().minusDays(1)).build()
        whenever(turnRepository.findByDateBefore(any())).thenReturn(listOf(pastTurn))

        reminderService.sendReminders()

        verify(turnRepository).deleteAll(eq(listOf(pastTurn)))
    }
}
