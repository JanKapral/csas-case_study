package cz.kapraljan.casestudy.adapters.db.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "eligibility_attempts")
data class EligibilityAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,
    val clientId: String,
    val attemptTime: LocalDateTime,
    val eligible: Boolean
)

