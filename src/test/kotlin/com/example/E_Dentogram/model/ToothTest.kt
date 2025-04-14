package com.example.E_Dentogram.model

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import kotlin.test.Test


class ToothTest{

    @Test
    fun `should create a tooth with a min valid number`() {
        val tooth = Tooth.ToothBuilder().number(1)

        assertEquals(1, tooth.number)
    }

    @Test
    fun `should create a tooth with a max valid Patient`() {
        val tooth = Tooth.ToothBuilder().number(52)

        assertEquals(52, tooth.number)
    }

    @Test
    fun `should throw exception for a tooth with min invalid number `() {

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Tooth.ToothBuilder().number(0)
        }
        assertEquals("The Tooth number is not valid.", exception.message)
    }

    @Test
    fun `should throw exception for a tooth with max invalid number `() {

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            Tooth.ToothBuilder().number(53)
        }
        assertEquals("The Tooth number is not valid.", exception.message)
    }

    @Test
    fun `should create a tooth with a valid up`() {
        val tooth = Tooth.ToothBuilder().up(ToothState.HEALTHY)

        assertEquals(ToothState.HEALTHY, tooth.up)
    }

    @Test
    fun `should create a tooth with a valid right`() {
        val tooth = Tooth.ToothBuilder().right(ToothState.HEALTHY)

        assertEquals(ToothState.HEALTHY, tooth.right)
    }

    @Test
    fun `should create a tooth with a valid down`() {
        val tooth = Tooth.ToothBuilder().down(ToothState.HEALTHY)

        assertEquals(ToothState.HEALTHY, tooth.down)
    }

    @Test
    fun `should create a tooth with a valid left`() {
        val tooth = Tooth.ToothBuilder().left(ToothState.HEALTHY)

        assertEquals(ToothState.HEALTHY, tooth.left)
    }

    @Test
    fun `should create a tooth with a valid center`() {
        val tooth = Tooth.ToothBuilder().center(ToothState.HEALTHY)

        assertEquals(ToothState.HEALTHY, tooth.center)
    }

    @Test
    fun `should create a tooth with a valid Patient`() {
        val patient = mock(Patient::class.java)

        val tooth = Tooth.ToothBuilder().patient(patient)

        assertEquals(patient, tooth.patient)
    }



}