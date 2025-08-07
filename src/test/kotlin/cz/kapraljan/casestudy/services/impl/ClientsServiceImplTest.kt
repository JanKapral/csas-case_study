package cz.kapraljan.casestudy.services.impl

import cz.kapraljan.casestudy.adapters.customers.ClientsRepository
import cz.kapraljan.casestudy.adapters.customers.models.ClientResponseDto
import cz.kapraljan.casestudy.adapters.customers.models.GenderResponseDto
import cz.kapraljan.casestudy.services.models.Client
import cz.kapraljan.casestudy.services.models.Gender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class ClientsServiceImplTest {

    @Mock
    private lateinit var clientsRepository: ClientsRepository

    @InjectMocks
    private lateinit var clientsService: ClientsServiceImpl

    @Test
    fun `getClientById should return client when repository returns client`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val clientDto = ClientResponseDto(LocalDate.now(), 1, "Jan", null, GenderResponseDto.M, null, false, null, "Kapral", "123")
        val expectedClient = Client("123", "Jan", "Kapral", LocalDate.now(), Gender.M, null, null, false, null, 1)
        whenever(clientsRepository.getClientById(clientId, correlationId)).thenReturn(clientDto)

        val result = clientsService.getClientById(clientId, correlationId)

        assertEquals(expectedClient, result)
    }

    @Test
    fun `getClientById should throw exception when repository throws exception`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val exception = RuntimeException("Client not found")
        whenever(clientsRepository.getClientById(clientId, correlationId)).thenThrow(exception)

        val thrownException = assertThrows<RuntimeException> {
            clientsService.getClientById(clientId, correlationId)
        }
        assertEquals("Client not found", thrownException.message)
    }
}
