package cz.kapraljan.casestudy.adapters.api

import cz.kapraljan.casestudy.adapters.api.dtos.EligibilityResponseDto
import cz.kapraljan.casestudy.services.EligibilityService
import cz.kapraljan.casestudy.services.models.toDto
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/eligibility")
class EligibilityController(private val eligibilityService: EligibilityService) {

    @GetMapping
    fun getEligibility(
        @RequestHeader("clientId") clientId: String,
        @RequestHeader("correlation-id", required = false) correlationId: UUID?
    ): ResponseEntity<EligibilityResponseDto> {
        val result = eligibilityService.checkEligibility(clientId, correlationId)

        val headers = HttpHeaders().apply {
            correlationId?.let { set("correlation-id", it.toString()) }
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(EligibilityResponseDto(result.eligible, result.reasons.map { it.toDto() }))
    }
}