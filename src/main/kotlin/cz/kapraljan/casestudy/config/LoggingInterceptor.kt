package cz.kapraljan.casestudy.config

import cz.kapraljan.casestudy.services.LoggingService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingInterceptor(
    private val loggingService: LoggingService,
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val correlationId = request.getHeader("correlation-id")
        val method = request.method
        val url = request.requestURL.toString()

        val requestPayload = buildString {
            append("Headers: ${getHeadersAsString(request)}")

            val requestBody = getRequestBody(request)
            if (requestBody.isNotEmpty()) {
                append("\nRequest Body: $requestBody")
            }
        }

        loggingService.logRequest(method, url, requestPayload, correlationId)
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val correlationId = request.getHeader("correlation-id")
        val method = request.method
        val url = request.requestURL.toString()
        val status = response.status

        val responsePayload = buildString {
            append("Status: $status")

            val responseBody = getResponseBody(response)
            if (responseBody.isNotEmpty()) {
                append("\nResponse Body: $responseBody")
            }
        }

        loggingService.logResponse(method, url, responsePayload, status, correlationId)
    }

    private fun getRequestBody(request: HttpServletRequest): String {
        return try {
            when (request) {
                is ContentCachingRequestWrapper -> {
                    val content = request.contentAsByteArray
                    if (content.isNotEmpty()) {
                        String(content, Charsets.UTF_8)
                    } else ""
                }
                else -> {
                    val contentType = request.contentType
                    val contentLength = request.contentLength
                    if (contentLength > 0) {
                        "Request body present (Content-Type: $contentType, Length: $contentLength) but not cached"
                    } else ""
                }
            }
        } catch (e: Exception) {
            "Failed to read request body: ${e.message}"
        }
    }

    private fun getResponseBody(response: HttpServletResponse): String {
        return try {
            when (response) {
                is ContentCachingResponseWrapper -> {
                    val content = response.contentAsByteArray
                    if (content.isNotEmpty()) {
                        String(content, Charsets.UTF_8)
                    } else ""
                }
                else -> ""
            }
        } catch (e: Exception) {
            "Failed to read response body: ${e.message}"
        }
    }

    private fun getHeadersAsString(request: HttpServletRequest): String {
        val headers = mutableMapOf<String, String>()
        request.headerNames.asIterator().forEach { headerName ->
            headers[headerName] = request.getHeader(headerName)
        }
        return try {
            objectMapper.writeValueAsString(headers)
        } catch (_: Exception) {
            headers.toString()
        }
    }
}
