package cz.kapraljan.casestudy.adapters.accounts

import cz.kapraljan.casestudy.adapters.accounts.models.ClientDto
import cz.kapraljan.casestudy.adapters.accounts.models.CurrencyDto
import cz.kapraljan.casestudy.adapters.accounts.models.GetAccountsResponseDto
import cz.kapraljan.casestudy.adapters.accounts.models.InternationalAccountDto
import cz.kapraljan.casestudy.adapters.accounts.models.NationalAccountDto
import cz.kapraljan.casestudy.adapters.common.RestTemplateHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Instant
import java.util.*

@Repository
class AccountsRepository(
    @Autowired private val restTemplateHelper: RestTemplateHelper
) {

    @Value("\${accounts.api.base-url}")
    private lateinit var baseUrl: String

    @Value("\${accounts.api.api-key}")
    private lateinit var apiKey: String

    fun getAccountsByClientId(clientId: String, correlationId: UUID?): GetAccountsResponseDto {
        val uri: URI = UriComponentsBuilder.fromUriString(baseUrl)
            .path("/accounts")
            .build()
            .toUri()


        return restTemplateHelper.executeRestCall(
            clientId = clientId,
            uri = uri,
            responseType = GetAccountsResponseDto::class.java,
            apiKey = apiKey,
            serviceName = "Accounts",
            correlationId = correlationId
        )
    }
}