package cz.kapraljan.casestudy.services

import cz.kapraljan.casestudy.services.models.Account
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface AccountsService {
    fun getAccountsByClientId(clientId: String, correlationId: UUID?): List<Account>
}