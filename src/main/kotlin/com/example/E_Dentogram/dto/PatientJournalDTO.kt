package com.example.E_Dentogram.dto

import com.example.E_Dentogram.model.PatientJournal

class PatientJournalDTO(
    val journal: List<PatientJournal>,
    val pageSize: Int,
    val total: Long
)
