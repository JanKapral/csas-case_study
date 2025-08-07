package cz.kapraljan.casestudy.config

import cz.kapraljan.casestudy.services.LoggingService
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.nio.charset.StandardCharsets

@Component
class RestTemplateLoggingInterceptor(
    private val loggingService: LoggingService
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val correlationId = request.headers["correlation-id"]?.firstOrNull()
        val method = request.method.toString()
        val url = request.uri.toString()

        val requestPayload = if (body.isNotEmpty()) {
            String(body, StandardCharsets.UTF_8)
        } else {
            "Headers: ${request.headers}"
        }

        loggingService.logExternalApiCall(method, url, requestPayload, correlationId)

        val response = execution.execute(request, body)

        val responsePayload = try {
            val responseBody = response.body.bufferedReader().use(BufferedReader::readText)
            responseBody
        } catch (_: Exception) {
            "Response body could not be read"
        }

        loggingService.logExternalApiResponse(
            method,
            url,
            responsePayload,
            response.statusCode.value(),
            correlationId
        )

        return response
    }
}
