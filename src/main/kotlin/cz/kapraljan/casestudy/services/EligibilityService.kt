package cz.kapraljan.casestudy.services

import cz.kapraljan.casestudy.services.models.EligibilityResult
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface EligibilityService {
    fun checkEligibility(clientId: String, correlationId: UUID?): EligibilityResult
}