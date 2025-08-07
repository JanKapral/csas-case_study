package cz.kapraljan.casestudy.adapters.customers

import cz.kapraljan.casestudy.adapters.common.RestTemplateHelper
import cz.kapraljan.casestudy.adapters.customers.models.ClientResponseDto
import cz.kapraljan.casestudy.adapters.customers.models.GenderResponseDto
import cz.kapraljan.casestudy.exceptions.ClientErrorException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils
import java.net.URI
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class ClientsRepositoryTest {

    @Mock
    private lateinit var restTemplateHelper: RestTemplateHelper

    @InjectMocks
    private lateinit var clientsRepository: ClientsRepository

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(clientsRepository, "baseUrl", "http://localhost:8080")
        ReflectionTestUtils.setField(clientsRepository, "apiKey", "test-api-key")
    }

    @Test
    fun `getClientById should return client when api returns client`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val expectedResponse =
            ClientResponseDto(LocalDate.now(), 1, "Jan", null, GenderResponseDto.M, null, false, null, "Kapral", "123")

        whenever(
            restTemplateHelper.executeRestCall(
                clientId = eq(clientId),
                uri = any<URI>(),
                responseType = eq(ClientResponseDto::class.java),
                apiKey = eq("test-api-key"),
                serviceName = eq("Customers"),
                correlationId = eq(correlationId)
            )
        ).thenReturn(expectedResponse)

        val result = clientsRepository.getClientById(clientId, correlationId)

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `getClientById should throw exception when api returns null body`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()

        doAnswer { throw ClientErrorException(clientId, "Customers", 404) }
            .whenever(restTemplateHelper).executeRestCall(
                clientId = eq(clientId),
                uri = any<URI>(),
                responseType = eq(ClientResponseDto::class.java),
                apiKey = eq("test-api-key"),
                serviceName = eq("Customers"),
                correlationId = eq(correlationId)
            )

        val exception = assertThrows(ClientErrorException::class.java) {
            clientsRepository.getClientById(clientId, correlationId)
        }
        assertEquals("Client error in Customers service: HTTP 404 for client '$clientId'", exception.message)
    }
}
