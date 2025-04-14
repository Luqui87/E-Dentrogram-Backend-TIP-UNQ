package com.example.E_Dentogram.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*
import java.util.regex.Pattern

@Entity
@Table(name = "patient_table")
class Patient(builder: PatientBuilder) {

    @Id
    var medicalRecord: Int? = builder.medicalRecord

    @Column
    var dni: Int? = builder.dni

    @Column
    var name: String? = builder.name

    @Column
    var address: String? = builder.address

    @Column
    var birthdate: LocalDate? = builder.birthdate

    @Column
    var telephone: Int? = builder.telephone

    @Column
    var email: String? = builder.email

    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var teeth: MutableList<Tooth>? = builder.teeth


    class PatientBuilder {
        var dni: Int? = null
            private set
        var medicalRecord: Int? = null
            private set
        var name: String? = null
            private set
        var address: String? = null
            private set
        var birthdate: LocalDate? = null
            private set
        var telephone: Int? = null
            private set
        var email: String? = null
            private set
        var teeth: MutableList<Tooth>? = null
            private set

        fun dni(dni: Int) = apply {
            require(isValidDni(dni)) { "The DNI length is no correct." }
            this.dni = dni
        }

        fun medicalRecord(medicalRecord: Int) = apply {
            this.medicalRecord = medicalRecord
        }

        fun name(name: String) = apply {
            this.name = name
        }

        fun address(address: String) = apply {
            this.address = address
        }

        fun birthdate(birthdate: LocalDate) = apply {
            require(isValidBirthdate(birthdate))  { "The birthdate is invalid." }
            this.birthdate = birthdate
        }

        fun telephone(telephone: Int) = apply {
            require(isValidTelephone(telephone)) { "The telephone number is short." }
            this.telephone = telephone
        }

        fun email(email: String) = apply {
            require(isValidEmail(email)) { "The email format is not valid." }
            this.email = email
        }

        fun teeth(teeth: MutableList<Tooth>) = apply {
            require(isValidTeeth(teeth)) { "The amount of teeth is not valid." }
            this.teeth = teeth
        }


        private fun isValidDni(dni: Int): Boolean {
            return dni.toString().length == 8
        }

        private fun isValidBirthdate(birthdate: LocalDate): Boolean {
            return birthdate.isBefore(LocalDate.now())
        }

        private fun isValidTelephone(telephone: Int): Boolean {
            return telephone.toString().length >= 8
        }

        private fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            val pattern = Pattern.compile(emailRegex)
            return pattern.matcher(email).matches()
        }

        private fun isValidTeeth(teeth: List<Tooth>): Boolean {
            return teeth.size <= 52
        }

        fun build(): Patient {
            return Patient(this)
        }
    }

}
