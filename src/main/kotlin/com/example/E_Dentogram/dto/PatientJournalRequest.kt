package com.example.E_Dentogram.dto

import java.time.LocalDateTime


class PatientJournalRequest(
    var tags: List<String>,
    val log: String,
    val date: LocalDateTime
)