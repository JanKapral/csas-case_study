package cz.kapraljan.casestudy.services

import cz.kapraljan.casestudy.exceptions.ClientErrorException
import cz.kapraljan.casestudy.services.models.Client
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface ClientsService {
    @Throws(ClientErrorException::class)
    fun getClientById(id: String, correlationId: UUID?): Client
}