package com.example.E_Dentogram.model

enum class Tag {
    GENERAL_REVIEW,
    SURGERY;

    companion object {
        fun stringToState(state: String): Tag {
            return Tag.valueOf(state.uppercase())
        }
    }
}