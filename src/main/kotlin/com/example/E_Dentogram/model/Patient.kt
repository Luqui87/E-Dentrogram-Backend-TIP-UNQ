package com.example.E_Dentogram.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate
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
    var telephone: Long? = builder.telephone

    @Column
    var email: String? = builder.email

    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var teeth: MutableList<Tooth>? = builder.teeth

    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var historial: MutableList<PatientRecord>? = builder.historical

    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    var journal: MutableList<PatientJournal>? = builder.journal

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "dentist_id", nullable = false)
    var dentist: Dentist? = builder.dentist


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
        var telephone: Long? = null
            private set
        var email: String? = null
            private set
        var teeth: MutableList<Tooth>? = null
            private set
        var historical: MutableList<PatientRecord>? = null
            private set
        var journal: MutableList<PatientJournal>? = null
            private set
        var dentist: Dentist? = null
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

        fun telephone(telephone: Long) = apply {
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

        fun historial(historial: MutableList<PatientRecord>) = apply {
            this.historical = historial
        }


        fun dentist(dentist: Dentist) = apply {
            this.dentist = dentist
        }


        private fun isValidDni(dni: Int): Boolean {
            return dni.toString().length > 0
        }

        private fun isValidBirthdate(birthdate: LocalDate): Boolean {
            return birthdate.isBefore(LocalDate.now())
        }

        private fun isValidTelephone(telephone: Long): Boolean {
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

    fun updateDentist(dentist: Dentist) {
        this.dentist = dentist
    }

    fun update(telephone: Long, name: String, email: String, address: String) {
        this.telephone = telephone
        this.name = name
        this.email = email
        this.address = address
    }


}
