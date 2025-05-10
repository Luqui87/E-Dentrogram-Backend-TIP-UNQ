package com.example.E_Dentogram.model

enum class SpecialToothState {
    NOTHING,
    FILTERED_CROWNS,
    ORTHODONTICS,
    DENTAL_CROWNS,
    ROOT_CANAL_TREATMENT,
    DENTAL_CROWNS_WITH_ROOT_CANAL_TREATMENT;


    companion object {
        fun stringToState(state: String): SpecialToothState {
            return SpecialToothState.valueOf(state.uppercase())
        }
    }

}

