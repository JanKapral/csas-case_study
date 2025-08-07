package cz.kapraljan.casestudy.adapters.accounts

import cz.kapraljan.casestudy.adapters.accounts.models.ClientDto
import cz.kapraljan.casestudy.adapters.accounts.models.CurrencyDto
import cz.kapraljan.casestudy.adapters.accounts.models.GetAccountsResponseDto
import cz.kapraljan.casestudy.adapters.accounts.models.InternationalAccountDto
import cz.kapraljan.casestudy.adapters.accounts.models.NationalAccountDto
import cz.kapraljan.casestudy.adapters.common.RestTemplateHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccountsRepositoryTest {

    @Mock
    private lateinit var restTemplateHelper: RestTemplateHelper

    @InjectMocks
    private lateinit var accountsRepository: AccountsRepository

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(accountsRepository, "baseUrl", "http://localhost:8080")
        ReflectionTestUtils.setField(accountsRepository, "apiKey", "test-api-key")
    }

    @Test
    fun `getAccountsByClientId should return accounts when api returns accounts`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now()
        val expectedResponse = GetAccountsResponseDto(
            client = ClientDto("123", "some-owner"),
            accounts = listOf(
                NationalAccountDto("123456", closingDate, "prefix", "number", "bank code"),
                InternationalAccountDto("DE123456", closingDate, "iban", CurrencyDto.CZK)
            )
        )

        whenever(
            restTemplateHelper.executeRestCall(
                clientId = any<String>(),
                uri = any<java.net.URI>(),
                responseType = any<Class<GetAccountsResponseDto>>(),
                apiKey = any<String>(),
                serviceName = any<String>(),
                correlationId = org.mockito.kotlin.anyOrNull()
            )
        ).thenReturn(expectedResponse)

        val result = accountsRepository.getAccountsByClientId(clientId, correlationId)

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `getAccountsByClientId should throw exception when api returns null body`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()

        whenever(
            restTemplateHelper.executeRestCall(
                clientId = any<String>(),
                uri = any<java.net.URI>(),
                responseType = any<Class<GetAccountsResponseDto>>(),
                apiKey = any<String>(),
                serviceName = any<String>(),
                correlationId = org.mockito.kotlin.anyOrNull()
            )
        ).thenThrow(RuntimeException("Accounts not found for ID: $clientId"))

        // When & Then
        val exception = assertThrows(RuntimeException::class.java) {
            accountsRepository.getAccountsByClientId(clientId, correlationId)
        }
        assertEquals("Accounts not found for ID: $clientId", exception.message)
    }
}
