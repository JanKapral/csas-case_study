package cz.kapraljan.casestudy.services.impl

import cz.kapraljan.casestudy.adapters.accounts.AccountsRepository
import cz.kapraljan.casestudy.adapters.accounts.models.*
import cz.kapraljan.casestudy.services.models.Currency
import cz.kapraljan.casestudy.services.models.InternationalAccount
import cz.kapraljan.casestudy.services.models.NationalAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccountsServiceImplTest {

    @Mock
    private lateinit var accountsRepository: AccountsRepository

    @InjectMocks
    private lateinit var accountsService: AccountsServiceImpl

    @Test
    fun `getAccountsByClientId should return accounts when repository returns accounts`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now()
        val accountsResponse = GetAccountsResponseDto(
            client = ClientDto("123", "some-owner"),
            accounts = listOf(
                NationalAccountDto("123456", closingDate, "prefix", "number", "bank code"),
                InternationalAccountDto("DE123456", closingDate, "iban", CurrencyDto.CZK)
            )
        )
        val expectedAccounts = listOf(
            NationalAccount("123456", closingDate, "prefix", "number", "bank code"),
            InternationalAccount("DE123456", closingDate, "iban", Currency.CZK)
        )

        whenever(accountsRepository.getAccountsByClientId(clientId, correlationId)).thenReturn(accountsResponse)

        val result = accountsService.getAccountsByClientId(clientId, correlationId)

        assertEquals(expectedAccounts, result)
    }

    @Test
    fun `getAccountsByClientId should return empty list when repository returns no accounts`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val accountsResponse = GetAccountsResponseDto(
            client = ClientDto("123", "some-owner"),
            accounts = emptyList()
        )
        whenever(accountsRepository.getAccountsByClientId(clientId, correlationId)).thenReturn(accountsResponse)

        val result = accountsService.getAccountsByClientId(clientId, correlationId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAccountsByClientId should throw exception when repository throws exception`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val exception = RuntimeException("Accounts not found")
        whenever(accountsRepository.getAccountsByClientId(clientId, correlationId)).thenThrow(exception)

        val thrownException = assertThrows<RuntimeException> {
            accountsService.getAccountsByClientId(clientId, correlationId)
        }
        assertEquals("Accounts not found", thrownException.message)
    }
}
