package com.example.E_Dentogram.model

enum class Tag {
    GENERAL_REVIEW,
    SURGERY;

    companion object {
        fun stringToState(tag: String): Tag {
            try {
                return Tag.valueOf(tag.uppercase())
            }catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid tag : $tag")
            }

        }
    }
}
