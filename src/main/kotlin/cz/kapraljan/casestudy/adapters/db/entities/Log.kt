package cz.kapraljan.casestudy.adapters.db.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "logs")
data class Log(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    val timestamp: LocalDateTime = LocalDateTime.now(),
    val type: String,
    val method: String?,
    val url: String?,
    @Column(columnDefinition = "TEXT")
    val payload: String?,
    val status: Int?,
    val correlationId: String?
)