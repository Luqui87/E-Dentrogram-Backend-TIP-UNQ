package com.example.E_Dentogram.model

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import kotlin.test.Test

class PatientRecordTest{

    @Test
    fun `should create a dentist with a valid date`() {
        val date = LocalDateTime.now()
        val record = PatientRecord.PatientRecordBuilder().date(date)

        assertEquals(date, record.date)
    }

    @Test
    fun `should create a dentist with a valid tooth_number`() {
        val record = PatientRecord.PatientRecordBuilder().tooth_number(52)

        assertEquals(52, record.tooth_number)
    }

    @Test
    fun `should create a dentist with a valid before`() {
        val before : MutableList<String> = mutableListOf()
        val record = PatientRecord.PatientRecordBuilder().before(before)

        assertEquals(before, record.before)
    }

    @Test
    fun `should create a dentist with a valid after`() {
        val after : MutableList<String> = mutableListOf()
        val record = PatientRecord.PatientRecordBuilder().after(after)

        assertEquals(after, record.after)
    }

    @Test
    fun `should create a dentist with a valid dentist name`() {
        val record = PatientRecord.PatientRecordBuilder().dentistName("julio")

        assertEquals("julio", record.dentistName)
    }

    @Test
    fun `should create a dentist with a valid patient`() {
        val patient : Patient = mock()
        val record = PatientRecord.PatientRecordBuilder().patient(patient)

        assertEquals(patient, record.patient)
    }


}