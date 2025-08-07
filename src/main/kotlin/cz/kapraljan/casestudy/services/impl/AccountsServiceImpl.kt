package cz.kapraljan.casestudy.services.impl

import cz.kapraljan.casestudy.adapters.accounts.AccountsRepository
import cz.kapraljan.casestudy.adapters.accounts.models.InternationalAccountDto
import cz.kapraljan.casestudy.adapters.accounts.models.NationalAccountDto
import cz.kapraljan.casestudy.adapters.accounts.models.toDomainModel
import cz.kapraljan.casestudy.services.AccountsService
import cz.kapraljan.casestudy.services.models.Account
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountsServiceImpl(private val accountsRepository: AccountsRepository) : AccountsService {
    override fun getAccountsByClientId(clientId: String, correlationId: UUID?): List<Account> {
        val response = accountsRepository.getAccountsByClientId(clientId, correlationId)
        return response.accounts.map {
            when (it) {
                is NationalAccountDto -> it.toDomainModel()
                is InternationalAccountDto -> it.toDomainModel()
            }
        }
    }
}
