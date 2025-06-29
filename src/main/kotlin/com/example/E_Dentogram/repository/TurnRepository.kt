package com.example.E_Dentogram.repository

import com.example.E_Dentogram.model.Turn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TurnRepository : JpaRepository<Turn, Int> {

    fun findByPatient_MedicalRecordAndDate(patientId: Int, date: LocalDateTime): Turn?

    fun findByDateBetween(start: LocalDateTime, end: LocalDateTime): List<Turn>

    fun findByDateBefore(dateTime: LocalDateTime): List<Turn>
}