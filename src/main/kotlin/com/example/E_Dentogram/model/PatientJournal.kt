package com.example.E_Dentogram.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "journal_table")
class PatientJournal(builder: PatientJournalBuilder) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var tags: List<Tag>? = builder.tags

    @Column
    var log : String? = builder.log

    @Column
    var date : LocalDateTime? = builder.date

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    var patient: Patient? = builder.patient


    class PatientJournalBuilder {
        var tags: List<Tag>? = null
            private set
        var log: String? = null
            private set
        var date: LocalDateTime? = null
            private set
        var patient: Patient? = null
            private set

        fun tags(tags: List<Tag>) = apply {
            this.tags = tags
        }

        fun log(log: String) = apply {
            this.log = log
        }

        fun date(date: LocalDateTime) = apply {
            this.date = date
        }

        fun patient(patient: Patient) = apply {
            this.patient = patient
        }

        fun build(): PatientJournal {
            return PatientJournal(this)
        }
    }



}