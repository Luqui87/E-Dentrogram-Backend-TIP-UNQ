package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.ToothDTO
import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.model.ToothState
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.request.ToothRequest
import com.example.E_Dentogram.repository.ThoothRepository
import jakarta.annotation.Generated

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Generated
@Service
@Transactional
class ToothService {

    @Autowired
    lateinit var toothRepository : ThoothRepository

    @Autowired
    lateinit var patientRepository : PatientRepository


    fun allTooth(): List<ToothDTO> {
        val teeth = toothRepository.findAll()
        return teeth.map { tooth -> ToothDTO.fromModel(tooth) }
    }

    fun teeth(medicalRecord: Int): List<ToothDTO> {
        if (patientRepository.existsById(medicalRecord)) {
            var teeth = toothRepository.findByPatientMedicalRecord(medicalRecord)
            return teeth.map { tooth -> ToothDTO.fromModel(tooth) }
        }
        else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist")
        }

    }

    @Transactional
    fun updateTeeth(medicalRecord: Int, teethRequests: List<ToothRequest>): List<ToothDTO> {
        val patient = patientRepository.findById(medicalRecord)
        .orElseThrow {  throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist")}

        val updatedTeeth = teethRequests.map { tooth ->
            Tooth.ToothBuilder()
                .number(tooth.number)
                .patient(patient)
                .up(ToothState.stringToState(tooth.up))
                .left(ToothState.stringToState(tooth.left))
                .center(ToothState.stringToState(tooth.center))
                .right(ToothState.stringToState(tooth.right))
                .down(ToothState.stringToState(tooth.down))
                .build()
        }

         val saveTeeth = toothRepository.saveAll(updatedTeeth)

        return saveTeeth.map { tooth -> ToothDTO.fromModel(tooth) }
    }

}