package com.example.E_Dentogram.service

import com.example.E_Dentogram.model.Tooth
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

}