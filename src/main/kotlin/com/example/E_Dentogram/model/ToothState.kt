package com.example.E_Dentogram.model

enum class ToothState {
    HEALTHY,
    RESTORATION,
    CARIES;

    companion object {
        fun stringToState(state: String): ToothState {
            return ToothState.valueOf(state.uppercase())
        }
    }

}