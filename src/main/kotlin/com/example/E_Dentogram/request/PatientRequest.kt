package com.example.E_Dentogram.request

import jakarta.annotation.Generated
import java.time.LocalDate

@Generated
class PatientRequest (val medicalRecord: Int,
                      val dni: Int,
                      val name: String,
                      val address: String,
                      val birthdate: LocalDate,
                      val telephone: Int,
                      val email: String) {
}