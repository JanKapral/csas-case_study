package cz.kapraljan.casestudy.services.impl

import cz.kapraljan.casestudy.adapters.customers.ClientsRepository
import cz.kapraljan.casestudy.adapters.customers.models.toDomainModel
import cz.kapraljan.casestudy.services.ClientsService
import cz.kapraljan.casestudy.services.models.Client
import org.springframework.stereotype.Service
import java.util.*

@Service
class ClientsServiceImpl(private val clientsRepository: ClientsRepository) : ClientsService {
    override fun getClientById(id: String, correlationId: UUID?): Client {
        val dto = clientsRepository.getClientById(id, correlationId)
        return dto.toDomainModel()
    }
}
