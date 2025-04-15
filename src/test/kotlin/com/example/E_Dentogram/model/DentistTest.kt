package com.example.E_Dentogram.model

import org.junit.jupiter.api.Assertions.*
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


}