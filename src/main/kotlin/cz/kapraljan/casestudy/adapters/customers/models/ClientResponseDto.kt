package cz.kapraljan.casestudy.adapters.customers.models

import cz.kapraljan.casestudy.services.models.Client
import cz.kapraljan.casestudy.services.models.Gender as DomainGender
import java.time.LocalDate

data class ClientResponseDto(
    val birthDate: LocalDate,
    val clientVerificationLevel: Int?,
    val forename: String,
    val primaryEmail: String?,
    val gender: GenderResponseDto,
    val primaryPhone: String?,
    val pep: Boolean,
    val verifiedBy: String?,
    val surname: String,
    val clientId: String
)

enum class GenderResponseDto { M, F, NB, O}

fun ClientResponseDto.toDomainModel(): Client = Client(
    clientId = clientId,
    forename = forename,
    surname = surname,
    birthDate = birthDate,
    gender = DomainGender.valueOf(gender.name),
    primaryEmail = primaryEmail,
    primaryPhone = primaryPhone,
    pep = pep,
    verifiedBy = verifiedBy,
    clientVerificationLevel = clientVerificationLevel
)
