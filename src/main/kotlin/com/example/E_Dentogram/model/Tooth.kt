package com.example.E_Dentogram.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "tooth_table")
class Tooth(builder: ToothBuilder) {

    @Id
    var number: Int? = builder.number

    @Column
    var up : ToothState? = builder.up

    @Column
    var right : ToothState? = builder.right

    @Column
    var down : ToothState? = builder.down

    @Column
    var left : ToothState? = builder.left

    @Column
    var center : ToothState? = builder.center

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    var patient: Patient? = builder.patient


    class ToothBuilder {
        var number: Int? = null
            private set
        var up: ToothState? = null
            private set
        var right: ToothState? = null
            private set
        var down: ToothState? = null
            private set
        var left: ToothState? = null
            private set
        var center: ToothState? = null
            private set
        var patient: Patient? = null
            private set


        fun number(number: Int) = apply {
            require(isValidNumber(number)) { "The Tooth number is not valid." }
            this.number = number
        }

        fun up(up: ToothState) = apply {
            this.up = up
        }

        fun right(right: ToothState) = apply {
            this.right = right
        }

        fun down(down: ToothState) = apply {
            this.down = down
        }

        fun left(left: ToothState) = apply {
            this.left = left
        }

        fun center(center: ToothState) = apply {
            this.center = center
        }

        fun patient(patient: Patient) = apply {
            this.patient = patient
        }

        private fun isValidNumber(number: Int): Boolean {
            return number in 1..52
        }

        fun build(): Tooth {
            return Tooth(this)
        }
    }





    }