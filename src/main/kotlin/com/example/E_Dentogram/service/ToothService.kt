package com.example.E_Dentogram.service

import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.request.ToothRequest
import com.example.E_Dentogram.repository.ThoothRepository
import jakarta.annotation.Generated

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Generated
@Service
@Transactional
class ToothService {

    @Autowired
    lateinit var toothRepository : ThoothRepository


    fun allTooth(): List<Tooth> {
        val teeth = toothRepository.findAll()
        return teeth
    }

    fun teeth(medicalRecord: Int): List<Tooth> {
        val teeth = toothRepository.findByPatientMedicalRecord(medicalRecord)
        return teeth
    }

    @Transactional
    fun updateTeeth(medicalRecord: Int, teethRequests: List<ToothRequest>): List<Tooth> {

        val existingTeeth = toothRepository.findByPatientMedicalRecord(medicalRecord)

        // val updatedTeeth = actualizados

        // return toothRepository.saveAll(updatedTeeth)

        return  existingTeeth // Para que tipe
    }

}