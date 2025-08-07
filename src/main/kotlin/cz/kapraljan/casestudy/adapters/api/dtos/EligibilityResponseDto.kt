package cz.kapraljan.casestudy.adapters.api.dtos

data class EligibilityResponseDto(
    val eligible: Boolean,
    val reasons : List<ReasonResponseDto>
)

enum class ReasonResponseDto {NO_ADULT, NO_ACCOUNT}

