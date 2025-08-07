package cz.kapraljan.casestudy.services.impl

import cz.kapraljan.casestudy.services.AccountsService
import cz.kapraljan.casestudy.services.ClientsService
import cz.kapraljan.casestudy.services.EligibilityService
import cz.kapraljan.casestudy.services.models.Account
import cz.kapraljan.casestudy.services.models.Client
import cz.kapraljan.casestudy.services.models.EligibilityResult
import cz.kapraljan.casestudy.services.models.IneligibilityReason
import org.springframework.stereotype.Service
import java.util.*
import cz.kapraljan.casestudy.adapters.db.repositories.EligibilityAttemptRepository
import cz.kapraljan.casestudy.adapters.db.entities.EligibilityAttempt
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.Period

@Service
class EligibilityServiceImpl(
    private val clientsService: ClientsService,
    private val accountsService: AccountsService,
    private val eligibilityAttemptRepository: EligibilityAttemptRepository
) : EligibilityService {
    override fun checkEligibility(clientId: String, correlationId: UUID?): EligibilityResult {
        val client = clientsService.getClientById(clientId, correlationId)
        val accounts = accountsService.getAccountsByClientId(clientId, correlationId)

        val eligibilityResult = computeEligibility(client, accounts)

        eligibilityAttemptRepository.save(
            EligibilityAttempt(
                clientId = clientId,
                attemptTime = LocalDateTime.now(),
                eligible = eligibilityResult.eligible
            )
        )

        return eligibilityResult
    }

    private fun computeEligibility(client: Client, accounts: List<Account>): EligibilityResult {
        val reasons = mutableListOf<IneligibilityReason>()

        val age = Period.between(client.birthDate, LocalDate.now()).years
        if (age < 18) {
            reasons.add(IneligibilityReason.NO_ADULT)
        }

        if (accounts.isEmpty()) {
            reasons.add(IneligibilityReason.NO_ACCOUNT)
        }
        return EligibilityResult(reasons.isEmpty(), reasons)
    }
}
