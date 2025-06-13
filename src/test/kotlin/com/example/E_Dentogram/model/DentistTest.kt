package com.example.E_Dentogram.model

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.Test

class DentistTest{

    @Test
    fun `should create a dentist with a valid username`() {
        val dentist = Dentist.DentistBuilder().username("User1")

        assertEquals("User1", dentist.username)
    }

    @Test
    fun `should create a dentist with a valid password`() {
        val dentist = Dentist.DentistBuilder().password("Password1")

        assertEquals("Password1", dentist.password)
    }

    @Test
    fun `should throw exception for dentist with a short password`() {
        val exception = org.junit.jupiter.api.assertThrows<RuntimeException> {
            Dentist.DentistBuilder().password("Pass")
        }
        assertEquals("The password is not strong enough.", exception.message)
    }

    @Test
    fun `should create a dentist with a valid patients list`() {
        val patients : MutableList<Patient> = mock()
        val dentist = Dentist.DentistBuilder().patients(patients)

        assertEquals(patients, dentist.patients)
    }

    @Test
    fun `should remove the patient from the dentist's patients list`() {
        val patientToRemove = mock<Patient>()
        Mockito.`when`(patientToRemove.medicalRecord).thenReturn(200)

        val otherPatient = mock<Patient>()
        Mockito.`when`(otherPatient.medicalRecord).thenReturn(123)

        val patients = mutableListOf(patientToRemove, otherPatient)

        val dentist = Dentist.DentistBuilder()
            .patients(patients)
            .build()

        dentist.removePatient(200)

        assertFalse(dentist.patients!!.contains(patientToRemove))
        assertTrue(dentist.patients!!.contains(otherPatient))
        assertEquals(1, dentist.patients!!.size)
    }

    @Test
    fun `should add patient if not already in the list`() {
        val patient = mock<Patient>()
        val patients = mutableListOf<Patient>()
        val dentist = Dentist.DentistBuilder()
            .patients(patients)
            .build()

        dentist.addPatient(patient)

        assertTrue(dentist.patients!!.contains(patient))
        Mockito.verify(patient).updateDentist(dentist)
        assertEquals(1, dentist.patients!!.size)
    }

    @Test
    fun `should not add patient if already in the list`() {
        val patient = mock<Patient>()
        val patients = mutableListOf(patient)
        val dentist = Dentist.DentistBuilder()
            .patients(patients)
            .build()

        dentist.addPatient(patient)

        assertEquals(1, dentist.patients!!.size)
        Mockito.verify(patient, Mockito.never()).updateDentist(dentist)
    }

    @Test
    fun `should create a user with a valid email`() {
        val user = Dentist.DentistBuilder().email("Marcos.dias@example.com")

        assertNotNull(user)
        assertEquals("Marcos.dias@example.com", user.email)
    }

    @Test
    fun `should throw exception for email without a at sign `() {
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Dentist.DentistBuilder().email("MARCOSDIASEXAMPLE.com")
        }
        assertEquals("The email format is not valid.", exception.message)
    }

    @Test
    fun `should throw exception for email with space `() {
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Dentist.DentistBuilder().email("MARCOS DIASEXAMPLE.COM")
        }
        assertEquals("The email format is not valid.", exception.message)
    }

    @Test
    fun `should create a dentist with a valid name`() {
        val dentist = Dentist.DentistBuilder().name("User1_name")

        assertEquals("User1_name", dentist.name)
    }





}