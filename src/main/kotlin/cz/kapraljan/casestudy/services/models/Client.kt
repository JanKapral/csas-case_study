package cz.kapraljan.casestudy.services.models

import java.time.LocalDate

data class Client(
    val clientId: String,
    val forename: String,
    val surname: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val primaryEmail: String?,
    val primaryPhone: String?,
    val pep: Boolean,
    val verifiedBy: String?,
    val clientVerificationLevel: Int?
)

enum class Gender { M, F, NB, O }
