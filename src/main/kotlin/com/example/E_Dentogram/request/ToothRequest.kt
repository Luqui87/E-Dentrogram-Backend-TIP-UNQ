package com.example.E_Dentogram.request

import java.time.LocalDate

class ToothRequest(
        val number: Int,
        val up: String,
        val right: String,
        val down: String,
        val left: String,
        val center: String){}