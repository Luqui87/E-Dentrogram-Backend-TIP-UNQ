package com.example.E_Dentogram.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ToothStateConverter : AttributeConverter<ToothState, String> {

    override fun convertToDatabaseColumn(attribute: ToothState?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): ToothState? {
        return dbData?.let { ToothStateParser.stringToState(it) }
    }
}
