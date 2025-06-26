package com.example.E_Dentogram.model

import org.junit.jupiter.api.Assertions.*
import org.testcontainers.shaded.org.apache.commons.lang3.mutable.Mutable
import java.time.LocalDateTime
import kotlin.test.Test

class PatientJournalTest{

    @Test
    fun `should create a journalPatiente with a valid date`() {
        val date = LocalDateTime.now()
        val journal = PatientJournal.PatientJournalBuilder().date(date)

        assertEquals(date, journal.date)
    }

    @Test
    fun `should create a journalPatiente with a log`() {
        val journal = PatientJournal.PatientJournalBuilder().log("hola")

        assertEquals("hola", journal.log)
    }

    @Test
    fun `should create a dentist without tag`() {
        val tags : MutableSet<String> = mutableSetOf<String>()
        val journal = PatientJournal.PatientJournalBuilder().tags(tags)

        assertEquals(mutableSetOf<String>(), journal.tags)
    }

    @Test
    fun `should create a dentist with a valid after`() {
        val tags : MutableSet<String> = mutableSetOf("Revisión", "Blanqueamiento", "Blanqueamiento")
        val journal = PatientJournal.PatientJournalBuilder().tags(tags)

        assertEquals(mutableListOf("Revisión", "Blanqueamiento"), journal.tags)
    }


}

