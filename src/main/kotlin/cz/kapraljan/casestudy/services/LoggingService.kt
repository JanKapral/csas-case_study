package cz.kapraljan.casestudy.services

import cz.kapraljan.casestudy.adapters.db.entities.Log
import cz.kapraljan.casestudy.adapters.db.repositories.LogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoggingService(private val logRepository: LogRepository) {

    @Transactional
    fun logRequest(method: String, url: String, payload: String?, correlationId: String?) {
        val log = Log(
            type = "REQUEST",
            method = method,
            url = url,
            payload = payload,
            status = null,
            correlationId = correlationId
        )
        logRepository.save(log)
    }

    @Transactional
    fun logResponse(method: String, url: String, payload: String?, status: Int, correlationId: String?) {
        val log = Log(
            type = "RESPONSE",
            method = method,
            url = url,
            payload = payload,
            status = status,
            correlationId = correlationId
        )
        logRepository.save(log)
    }

    @Transactional
    fun logExternalApiCall(method: String, url: String, payload: String?, correlationId: String?) {
        val log = Log(
            type = "EXTERNAL_REQUEST",
            method = method,
            url = url,
            payload = payload,
            status = null,
            correlationId = correlationId
        )
        logRepository.save(log)
    }

    @Transactional
    fun logExternalApiResponse(method: String, url: String, payload: String?, status: Int, correlationId: String?) {
        val log = Log(
            type = "EXTERNAL_RESPONSE",
            method = method,
            url = url,
            payload = payload,
            status = status,
            correlationId = correlationId
        )
        logRepository.save(log)
    }
}
