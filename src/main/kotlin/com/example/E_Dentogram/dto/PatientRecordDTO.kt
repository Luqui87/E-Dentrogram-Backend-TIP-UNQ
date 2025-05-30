package com.example.E_Dentogram.dto

import com.example.E_Dentogram.model.PatientRecord

data class PatientRecordDTO(
    val records: List<PatientRecord>,
    val total: Long
)
