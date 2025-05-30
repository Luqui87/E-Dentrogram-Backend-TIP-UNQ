package com.example.E_Dentogram.model
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "record_table")
class PatientRecord(builder: PatientRecordBuilder) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var date : LocalDateTime? = builder.date

    @Column
    var tooth_number: Int? = builder.tooth_number

    @Convert(converter = StringListConverter::class)
    @Lob
    @Column(name = "before")
    var before: List<String>? = builder.before   // up - right - down - left - center - special

    @Convert(converter = StringListConverter::class)
    @Lob
    @Column(name = "after")
    var after : List<String>? = builder.after

    @Column
    var dentistName : String? = builder.dentistName

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    var patient: Patient? = builder.patient


    class PatientRecordBuilder {
        var date: LocalDateTime? = null
            private set
        var tooth_number: Int? = null
            private set
        var before: List<String>? = null
            private set
        var after: List<String>? = null
            private set
        var dentistName: String? = null
            private set
        var patient: Patient? = null
            private set


        fun date(date: LocalDateTime) = apply {
            this.date = date
        }

        fun tooth_number(tooth_number: Int) = apply {
            this.tooth_number = tooth_number
        }

        fun before(before: List<String>) = apply {
            this.before = before
        }

        fun after(after: List<String>) = apply {
            this.after = after
        }

        fun dentistName(dentistName: String) = apply {
            this.dentistName = dentistName
        }

        fun patient(patient: Patient) = apply {
            this.patient = patient
        }

        fun build(): PatientRecord {
            return PatientRecord(this)
        }
    }





}