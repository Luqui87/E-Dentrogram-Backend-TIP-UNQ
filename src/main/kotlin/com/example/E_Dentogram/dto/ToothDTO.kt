package com.example.E_Dentogram.dto

import com.example.E_Dentogram.model.Tooth
import java.time.LocalDate

class ToothDTO(
    val number: Int,
    val up: String,
    val right: String,
    val down: String,
    val left: String,
    val center: String
) {
    companion object{
    fun fromModel(tooth:Tooth): ToothDTO{
        return ToothDTO(
            tooth.number!!,
            tooth.up.toString(),
            tooth.right.toString(),
            tooth.down.toString(),
            tooth.left.toString(),
            tooth.center.toString()
        )
    }
    }
}
