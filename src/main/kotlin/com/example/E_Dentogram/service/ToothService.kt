package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.ToothDTO
import com.example.E_Dentogram.model.SpecialToothState
import com.example.E_Dentogram.model.Tooth
import com.example.E_Dentogram.model.ToothState
import com.example.E_Dentogram.model.ToothStateParser
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.repository.ToothRepository
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
    lateinit var toothRepository : ToothRepository

    @Autowired
    lateinit var patientRepository : PatientRepository

    @Transactional(readOnly=true)
    fun allTooth(): List<ToothDTO> {
        val teeth = toothRepository.findAll()
        return teeth.map { tooth -> ToothDTO.fromModel(tooth) }
    }

    @Transactional(readOnly=true)
    fun teeth(medicalRecord: Int): List<ToothDTO> {
        if (patientRepository.existsById(medicalRecord)) {
            val teeth = toothRepository.findByPatientMedicalRecord(medicalRecord)
            return teeth.map { tooth -> ToothDTO.fromModel(tooth) }
        }
        else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist")
        }

    }

    fun updateTeeth(medicalRecord: Int, toothDTO: ToothDTO): ToothDTO{
        val patient = patientRepository.findById(medicalRecord)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist") }

        val existingTooth = toothRepository.findByNumberAndPatientMedicalRecord(toothDTO.number, medicalRecord)

        val updatedTooth = try {
            val up = combineStates(existingTooth?.up, ToothStateParser.stringToState(toothDTO.up))
            val right = combineStates(existingTooth?.right, ToothStateParser.stringToState(toothDTO.right))
            val down = combineStates(existingTooth?.down, ToothStateParser.stringToState(toothDTO.down))
            val left = combineStates(existingTooth?.left, ToothStateParser.stringToState(toothDTO.left))
            val center = combineStates(existingTooth?.center, ToothStateParser.stringToState(toothDTO.center))

            Tooth.ToothBuilder()
                .number(toothDTO.number)
                .patient(patient)
                .up(up)
                .right(right)
                .down(down)
                .left(left)
                .center(center)
                .special(SpecialToothState.stringToState(toothDTO.special))
                .build()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data provided for teeth update", e)
        }

        val savedTooth = toothRepository.save(updatedTooth)

        return ToothDTO.fromModel(savedTooth)
    }

    private fun combineStates(oldState: ToothState?, newState: ToothState): ToothState {
        return if (oldState == null) {
            newState
        } else {
            oldState.combineWith(newState)
        }
    }

}