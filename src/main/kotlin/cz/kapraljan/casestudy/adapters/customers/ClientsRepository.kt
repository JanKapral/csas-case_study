package cz.kapraljan.casestudy.adapters.customers

import cz.kapraljan.casestudy.adapters.common.RestTemplateHelper
import cz.kapraljan.casestudy.adapters.customers.models.ClientResponseDto
import cz.kapraljan.casestudy.adapters.customers.models.GenderResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDate
import java.util.*

@Repository
class ClientsRepository(
    @Autowired private val restTemplateHelper: RestTemplateHelper
) {

    @Value("\${customers.api.base-url}")
    private lateinit var baseUrl: String

    @Value("\${customers.api.api-key}")
    private lateinit var apiKey: String

    fun getClientById(clientId: String, correlationId: UUID?): ClientResponseDto {
        val uri: URI = UriComponentsBuilder.fromUriString(baseUrl).path("/{clientId}").buildAndExpand(clientId).toUri()

        return restTemplateHelper.executeRestCall(
            clientId = clientId,
            uri = uri,
            responseType = ClientResponseDto::class.java,
            apiKey = apiKey,
            serviceName = "Customers",
            correlationId = correlationId
        )
    }
}

