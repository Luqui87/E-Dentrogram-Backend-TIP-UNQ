package com.example.E_Dentogram.model

import jakarta.persistence.*

@Entity
@Table(name = "document_table")
class Document(builder: DocumentBuilder) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var fileName: String = builder.fileName!!

    @Lob
    @Basic(fetch = FetchType.LAZY)
    var data: ByteArray = builder.data!!

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id")
    var dentist: Dentist = builder.dentist!!

    class DocumentBuilder {
        var fileName: String? = null
            private set
        var data: ByteArray? = null
            private set
        var dentist: Dentist? = null
            private set

        fun fileName(fileName: String) = apply {
            require(fileName.isNotBlank()) { "El nombre del archivo no puede estar vacío." }
            this.fileName = fileName
        }

        fun data(data: ByteArray) = apply {
            require(data.isNotEmpty()) { "El contenido del archivo no puede estar vacío." }
            this.data = data
        }

        fun dentist(dentist: Dentist) = apply {
            this.dentist = dentist
        }

        fun build(): Document {
            requireNotNull(fileName) { "El nombre del archivo es obligatorio." }
            requireNotNull(data) { "El contenido del archivo es obligatorio." }
            requireNotNull(dentist) { "El dentista asociado es obligatorio." }

            return Document(this)
        }
    }
}