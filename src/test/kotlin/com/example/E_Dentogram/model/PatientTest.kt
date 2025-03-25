package com.example.E_Dentogram.model

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import kotlin.test.Test

class PatientTest {

    @Test
    fun `should create a patient with a valid medicalRecord`() {
        val patient = Patient.PatientBuilder().medicalRecord(123)

        assertEquals(123, patient.medicalRecord)
    }

    @Test
    fun `should create a patient with a valid dni`() {
        val patient = Patient.PatientBuilder().dni(12345678)

        assertEquals(12345678, patient.dni)
    }

    @Test
    fun `should throw exception for patient with a short dni`() {
        val exception = assertThrows<RuntimeException> {
            Patient.PatientBuilder().dni(12345)
        }
        assertEquals("The DNI length is no correct.", exception.message)
    }

    @Test
    fun `should throw exception for patient with a long dni`() {
        val exception = assertThrows<RuntimeException> {
            Patient.PatientBuilder().dni(123456789)
        }
        assertEquals("The DNI length is no correct.", exception.message)
    }

    @Test
    fun `should create a patient with a valid name`() {
        val patient = Patient.PatientBuilder().name("Pedro")

        assertEquals("Pedro", patient.name)
    }

    @Test
    fun `should create a patient with a valid address`() {
        val patient = Patient.PatientBuilder().address("123 Main Street")

        assertEquals("123 Main Street", patient.address)
    }

    @Test
    fun `should create a patient with a valid birthdate`() {
        val date = LocalDate.of(1994,3,22)
        val patient = Patient.PatientBuilder().birthdate(date)

        assertEquals(date, patient.birthdate)
    }

    @Test
    fun `should throw exception for a patient with a future birthdate`() {
        val date = LocalDate.of(2222,2,22)

        val exception = assertThrows<RuntimeException> {
            Patient.PatientBuilder().birthdate(date)
        }
        assertEquals("The birthdate is invalid.", exception.message)
    }

    @Test
    fun `should create a patient with a valid telephone`() {
        val patient = Patient.PatientBuilder().telephone(12345678)

        assertEquals(12345678, patient.telephone)
    }

    @Test
    fun `should throw exception for short telephone`() {
        val exception = assertThrows<IllegalArgumentException> {
            Patient.PatientBuilder().telephone(1234567)
        }
        assertEquals("The telephone number is short.", exception.message)
    }

    @Test
    fun `should create a user with a valid email`() {
        val user = Patient.PatientBuilder().email("Marcos.dias@example.com")

        assertNotNull(user)
        assertEquals("Marcos.dias@example.com", user.email)
    }

    @Test
    fun `should throw exception for email without a at sign `() {
        val exception = assertThrows<IllegalArgumentException> {
            Patient.PatientBuilder().email("MARCOSDIASEXAMPLE.com")
        }
        assertEquals("The email format is not valid.", exception.message)
    }

    @Test
    fun `should throw exception for email with space `() {
        val exception = assertThrows<IllegalArgumentException> {
            Patient.PatientBuilder().email("MARCOS DIASEXAMPLE.COM")
        }
        assertEquals("The email format is not valid.", exception.message)
    }



}