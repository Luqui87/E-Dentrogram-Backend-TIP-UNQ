package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.TurnRequest
import com.example.E_Dentogram.model.Turn
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.repository.TurnRepository
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Generated
@Service
@Transactional
class TurnService {
    @Autowired
    lateinit var patientRepository: PatientRepository

    @Autowired
    lateinit var turnRepository: TurnRepository

    fun addTurn(request: TurnRequest): Turn {
        val patient = patientRepository.findById(request.patientId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found") }

        val turn = Turn.TurnBuilder()
            .date(request.date)
            .patient(patient)
            .build()

        return turnRepository.save(turn)
    }

    fun rescheduleTurn(request: TurnRequest, newDate: LocalDateTime): Turn {
        val patient = patientRepository.findById(request.patientId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found") }

        val turn = turnRepository.findByPatient_MedicalRecordAndDate(patient.medicalRecord!!, request.date)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Turn not found")

        turn.date = newDate
        return turnRepository.save(turn)
    }

}