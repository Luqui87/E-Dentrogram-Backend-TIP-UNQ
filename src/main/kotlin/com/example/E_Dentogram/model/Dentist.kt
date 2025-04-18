package com.example.E_Dentogram.model

import jakarta.persistence.*

@Entity
@Table(name = "dentist_table")
class Dentist(builder: DentistBuilder) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true, nullable = false)
    var username: String? = builder.username

    @Column
    var password: String? = builder.password

    @OneToMany(mappedBy = "dentist", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var patients: MutableList<Patient>? = builder.patients

    class DentistBuilder {
        var username: String? = null
            private set
        var password: String? = null
            private set
        var patients: MutableList<Patient>? = null
            private set

        fun username(username: String) = apply {
            this.username = username
        }

        fun password(password: String) = apply {
            require(isValidPassword(password)) { "The password is not strong enough." }
            this.password = password
        }

        fun patients(patients: MutableList<Patient>) = apply {
            this.patients = patients
        }

        private fun isValidPassword(password: String): Boolean {
            return password.length >= 8
        }

        fun build(): Dentist {
            return Dentist(this)
        }

    }

    fun addPatient(patient: Patient) {
        if (!patients!!.contains(patient)) {
            patients!!.add(patient)
            patient.updateDentist(this)
        }
    }

    fun removePatient(patientMedicalRecord: Int) {
        this.patients?.removeIf { it.medicalRecord == patientMedicalRecord }
    }

}