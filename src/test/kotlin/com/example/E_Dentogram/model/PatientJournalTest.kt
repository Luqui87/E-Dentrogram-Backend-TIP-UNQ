package com.example.E_Dentogram.model

import org.junit.jupiter.api.Assertions.*
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
        val tags : MutableList<Tag> = mutableListOf()
        val journal = PatientJournal.PatientJournalBuilder().tags(tags)

        assertEquals(mutableListOf<Tag>(), journal.tags)
    }

    @Test
    fun `should create a dentist with a valid after`() {
        val tags : MutableList<Tag> = mutableListOf(Tag.SURGERY,Tag.GENERAL_REVIEW)
        val journal = PatientJournal.PatientJournalBuilder().tags(tags)

        assertEquals(mutableListOf(Tag.SURGERY,Tag.GENERAL_REVIEW), journal.tags)
    }


}

