package cz.kapraljan.casestudy.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@Order(1)
class RequestResponseCachingFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val wrappedRequest = ContentCachingRequestWrapper(httpRequest)
        val wrappedResponse = ContentCachingResponseWrapper(httpResponse)

        try {
            chain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            // Important: copy the response content back to the original response
            wrappedResponse.copyBodyToResponse()
        }
    }
}
