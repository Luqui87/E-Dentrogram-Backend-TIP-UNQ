package com.example.E_Dentogram.dto

import java.time.LocalDateTime

class TurnRequest(
    val date: LocalDateTime,
    val patientId: Int
)