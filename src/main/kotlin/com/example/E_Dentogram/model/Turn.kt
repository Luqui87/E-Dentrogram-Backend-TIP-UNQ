package com.example.E_Dentogram.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "turn_table")
class Turn(builder: TurnBuilder) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var date : LocalDateTime? = builder.date

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    var patient: Patient? = builder.patient


    class TurnBuilder {
        var date: LocalDateTime? = null
            private set
        var patient: Patient? = null
            private set

        fun date(date: LocalDateTime) = apply {
            this.date = date
        }

        fun patient(patient: Patient) = apply {
            this.patient = patient
        }

        fun build(): Turn {
            return Turn(this)
        }
    }

}