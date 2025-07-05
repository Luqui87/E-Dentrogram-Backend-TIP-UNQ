package com.example.E_Dentogram.model

import jakarta.persistence.*
import java.util.regex.Pattern

@Entity
@Table(name = "dentist_table")
class Dentist(builder: DentistBuilder) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true, nullable = false)
    var username: String? = builder.username

    @Column
    var name: String? = builder.name

    @Column
    var password: String? = builder.password

    @Column(unique = true, nullable = false)
    var email: String? = builder.email

    var role: Role = Role.USER

    @ElementCollection
    var tags: List<String> = listOf("Revisión General",
        "Cirugía",
        "Limpieza",
        "Blanqueamiento",
        "Ortodoncia",
        "Fluor")

    @OneToMany(mappedBy = "dentist", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var patients: MutableList<Patient>? = builder.patients

    @OneToMany(mappedBy = "dentist", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var documents: MutableList<Document>? = builder.documents


    class DentistBuilder {
        var username: String? = null
            private set
        var name: String? = null
            private set
        var password: String? = null
            private set
        var email: String? = null
            private set
        var patients: MutableList<Patient>? = null
            private set
        var documents: MutableList<Document>? = null
            private set

        fun username(username: String) = apply {
            this.username = username
        }

        fun name(name: String) = apply {
            this.name = name
        }

        fun password(password: String) = apply {
            require(isValidPassword(password)) { "The password is not strong enough." }
            this.password = password
        }

        fun email(email: String) = apply {
            require(isValidEmail(email)) { "The email format is not valid." }
            this.email = email
        }

        fun patients(patients: MutableList<Patient>) = apply {
            this.patients = patients
        }

        fun documents(documents: MutableList<Document>) = apply {
            this.documents = documents
        }

        private fun isValidPassword(password: String): Boolean {
            return password.length >= 8
        }

        private fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            val pattern = Pattern.compile(emailRegex)
            return pattern.matcher(email).matches()
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

enum class Role{
    USER,ADMIN
}