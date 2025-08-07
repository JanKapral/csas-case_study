package cz.kapraljan.casestudy.services.models

import cz.kapraljan.casestudy.adapters.api.dtos.ReasonResponseDto

data class EligibilityResult(val eligible: Boolean, val reasons: List<IneligibilityReason>)
enum class IneligibilityReason { NO_ADULT, NO_ACCOUNT }

fun IneligibilityReason.toDto(): ReasonResponseDto = when (this) {
    IneligibilityReason.NO_ADULT -> ReasonResponseDto.NO_ADULT
    IneligibilityReason.NO_ACCOUNT -> ReasonResponseDto.NO_ACCOUNT
}
