package cz.kapraljan.casestudy.adapters.common

import cz.kapraljan.casestudy.exceptions.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*

@Component
class RestTemplateHelper(private val restTemplate: RestTemplate) {

    fun createHeaders(clientId: String, correlationId: UUID?, apiKey: String): HttpHeaders {
        return HttpHeaders().apply {
            set("clientId", clientId)
            if (correlationId != null) set("correlation-id", correlationId.toString())
            set("api-key", apiKey)
        }
    }

    fun <T> executeRestCall(
        clientId: String,
        uri: URI,
        responseType: Class<T>,
        apiKey: String,
        serviceName: String,
        correlationId: UUID? = null
    ): T {
        val headers = createHeaders(clientId, correlationId, apiKey)
        val entity = HttpEntity<Void>(headers)

        return try {
            val response = restTemplate.exchange(uri, HttpMethod.GET, entity, responseType)
            response.body ?: throw ClientErrorException(clientId, serviceName, 404)
        } catch (ex: HttpClientErrorException) {
            throw ClientErrorException(clientId, serviceName, ex.statusCode.value(), ex)
        } catch (ex: HttpServerErrorException) {
            throw ServerErrorException(serviceName, ex.statusCode.value(), ex)
        } catch (ex: ResourceAccessException) {
            throw ServerErrorException(serviceName, 503, ex)
        } catch (ex: Exception) {
            throw ServerErrorException(serviceName, 500, ex)
        }
    }
}
