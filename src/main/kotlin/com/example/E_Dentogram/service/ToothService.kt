package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.ToothDTO
import com.example.E_Dentogram.model.*
import com.example.E_Dentogram.repository.DentistRepository
import com.example.E_Dentogram.repository.PatientRecordRepository
import com.example.E_Dentogram.repository.PatientRepository
import com.example.E_Dentogram.repository.ToothRepository
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Generated
@Service
@Transactional
class ToothService {


    @Autowired
    lateinit var dentistRepository : DentistRepository

    @Autowired
    lateinit var toothRepository : ToothRepository

    @Autowired
    lateinit var patientRepository : PatientRepository

    @Autowired
    lateinit var patientRecordRepository : PatientRecordRepository

    @Autowired
    lateinit var tokenService: TokenService

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

    fun updateTeeth(medicalRecord: Int, toothDTO: ToothDTO, token:String): ToothDTO{
        val username = tokenService.extractUsername(token.substringAfter("Bearer "))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        try {
            validateRequest(toothDTO)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

        val dentist = dentistRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist")

        val patient = patientRepository.findById(medicalRecord)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist") }

        val existingTooth = toothRepository.findByNumberAndPatientMedicalRecord(toothDTO.number, medicalRecord)
        val updatedTooth = updatedTooth(toothDTO,patient)
        savePatientRecord(existingTooth,updatedTooth,patient,dentist.name!!)

        val savedTooth = toothRepository.save(updatedTooth)

        return ToothDTO.fromModel(savedTooth)
    }

    private fun validateRequest(toothDTO: ToothDTO) {
        val upState = ToothStateParser.stringToState(toothDTO.up)
        val states = listOf(
            ToothStateParser.stringToState(toothDTO.right),
            ToothStateParser.stringToState(toothDTO.down),
            ToothStateParser.stringToState(toothDTO.left),
            ToothStateParser.stringToState(toothDTO.center)
        )

        val isValid = if (upState.isTotalState() && upState != TotalToothState.HEALTHFUL) {
            states.all { it == upState }
        } else {
            states.all { !it.isTotalState() || it == TotalToothState.HEALTHFUL }
        }
        if (!isValid) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "This tooth state is invalid")
        }
    }


    private fun updatedTooth(toothDTO: ToothDTO,patient: Patient): Tooth {
        val updatedTooth = try {
            val up = ToothStateParser.stringToState(toothDTO.up)
            val right = ToothStateParser.stringToState(toothDTO.right)
            val down =  ToothStateParser.stringToState(toothDTO.down)
            val left =  ToothStateParser.stringToState(toothDTO.left)
            val center = ToothStateParser.stringToState(toothDTO.center)

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
        return updatedTooth
    }

    private fun savePatientRecord(before: Tooth?,after: Tooth?,patient: Patient,dentistName:String) {
        if (isNotTheSameTooth(before,after)) {
            val record = PatientRecord.PatientRecordBuilder()
                .date(LocalDateTime.now())
                .tooth_number(after!!.number!!)
                .before(toothList(before))
                .after(toothList(after))
                .dentistName(dentistName)
                .patient(patient)
                .build()
            patientRecordRepository.save(record)
        }
    }

    private fun isNotTheSameTooth(before: Tooth?,after: Tooth?) : Boolean{
        return  ( before == null && after != null) ||
                before!!.up != after!!.up ||
                before.right != after.right ||
                before.down != after.down ||
                before.left != after.left ||
                before.center != after.center ||
                before.special != after.special
    }

    private fun toothList(tooth : Tooth?):List<String>{
        return tooth?.toList() ?: listOf("HEALTHFUL","HEALTHFUL","HEALTHFUL","HEALTHFUL","HEALTHFUL","NOTHING")
    }

    fun teethAt(date: LocalDateTime, medicalRecord: Int): List<ToothDTO>? {
        if (patientRepository.existsById(medicalRecord)) {

            val timestamp = Timestamp.valueOf(date)

            val records = patientRecordRepository.findLatestToothRecordsUpToDate(medicalRecord, timestamp )
            return records.map { record -> ToothDTO(record.tooth_number!!, record.after!!) }
        }
        else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "This patient does not exist")
        }

   }

}