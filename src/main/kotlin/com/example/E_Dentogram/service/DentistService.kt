package com.example.E_Dentogram.service

import com.example.E_Dentogram.dto.DentistDTO
import com.example.E_Dentogram.model.Dentist
import com.example.E_Dentogram.repository.DentistRepository
import jakarta.annotation.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Generated
@Service
@Transactional
class DentistService {

    @Autowired
    lateinit var dentistRepository : DentistRepository

    @Transactional(readOnly=true)
    fun allDentist(): List<DentistDTO>? {
        val dentists = dentistRepository.findAll()

        val dentistDTOs = dentists.map {
            dentist -> DentistDTO(
                username = dentist.username!!,
                password = dentist.password!!)
        }
        return dentistDTOs
    }

    @Transactional(readOnly=true)
    fun getDentist(username: String): Dentist {
        return dentistRepository.findById(username).
        orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "This dentist does not exist") }
    }

}