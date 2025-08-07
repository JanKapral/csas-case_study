package cz.kapraljan.casestudy.services.impl

import cz.kapraljan.casestudy.adapters.db.repositories.EligibilityAttemptRepository
import cz.kapraljan.casestudy.services.AccountsService
import cz.kapraljan.casestudy.services.ClientsService
import cz.kapraljan.casestudy.services.models.*
import cz.kapraljan.casestudy.services.models.Currency
import cz.kapraljan.casestudy.exceptions.ClientErrorException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class EligibilityServiceImplTest {

    @Mock
    private lateinit var clientsService: ClientsService

    @Mock
    private lateinit var accountsService: AccountsService

    @Mock
    private lateinit var eligibilityAttemptRepository: EligibilityAttemptRepository

    @InjectMocks
    private lateinit var eligibilityService: EligibilityServiceImpl

    @Test
    fun `checkEligibility should return eligible when client is adult and has accounts`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400) // Tomorrow (future date)
        val client =
            Client("123", "Jan", "Kapral", LocalDate.now().minusYears(25), Gender.M, null, null, false, null, 1)
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.CZK))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should return eligible when client is exactly 18 years old`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(18), Gender.M, null, null, false, null, 1)
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.CZK))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should return not eligible when client is one day short of 18 years`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(18).plusDays(1), Gender.M, null, null, false, null, 1)
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.CZK))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertFalse(result.eligible)
        assertEquals(1, result.reasons.size)
        assertEquals(IneligibilityReason.NO_ADULT, result.reasons[0])
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should return not eligible when client is under 18 years old`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(10), Gender.M, null, null, false, null, 1)
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.CZK))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertFalse(result.eligible)
        assertEquals(1, result.reasons.size)
        assertEquals(IneligibilityReason.NO_ADULT, result.reasons[0])
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should return not eligible when client has no accounts`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val client =
            Client("123", "Jan", "Kapral", LocalDate.now().minusYears(25), Gender.M, null, null, false, null, 1)
        val accounts = emptyList<Account>()

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertFalse(result.eligible)
        assertEquals(1, result.reasons.size)
        assertEquals(IneligibilityReason.NO_ACCOUNT, result.reasons[0])
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should return not eligible with multiple reasons when client is under 18 and has no accounts`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(15), Gender.F, null, null, false, null, 1)
        val accounts = emptyList<Account>()

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertFalse(result.eligible)
        assertEquals(2, result.reasons.size)
        assertTrue(result.reasons.contains(IneligibilityReason.NO_ADULT))
        assertTrue(result.reasons.contains(IneligibilityReason.NO_ACCOUNT))
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should work with NationalAccount type`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(30), Gender.M, null, null, false, null, 1)
        val accounts = listOf(NationalAccount("456", closingDate, "123", "456789", "0100"))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should work with mixed account types`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(40), Gender.NB, null, null, false, null, 1)
        val accounts = listOf(
            NationalAccount("456", closingDate, "123", "456789", "0100"),
            InternationalAccount("789", closingDate, "CZ6508000000192000145399", Currency.EUR)
        )

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should work with null correlationId`() {
        val clientId = "123"
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(25), Gender.O, null, null, false, null, 1)
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.USD))

        `when`(clientsService.getClientById(clientId, null)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, null)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, null)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should work with client having all optional fields filled`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        val client = Client(
            clientId = "123",
            forename = "Jan",
            surname = "Kapral",
            birthDate = LocalDate.now().minusYears(35),
            gender = Gender.M,
            primaryEmail = "jan@example.com",
            primaryPhone = "+420123456789",
            pep = true,
            verifiedBy = "KYC_TEAM",
            clientVerificationLevel = 3
        )
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.GBP))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should work with leap year birth date`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val closingDate = Instant.now().plusSeconds(86400)
        // Test with leap year date (Feb 29)
        val client = Client("123", "Jan", "Kapral", LocalDate.of(2004, 2, 29), Gender.M, null, null, false, null, 1)
        val accounts = listOf(InternationalAccount("123", closingDate, "iban", currency = Currency.CHF))

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertTrue(result.eligible)
        assertTrue(result.reasons.isEmpty())
        verify(eligibilityAttemptRepository).save(any())
    }

    @Test
    fun `checkEligibility should throw exception when clientsService throws exception`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val exception = ClientErrorException(clientId, "Clients", 404)

        `when`(clientsService.getClientById(clientId, correlationId)).thenThrow(exception)

        val thrownException = assertThrows<ClientErrorException> {
            eligibilityService.checkEligibility(clientId, correlationId)
        }
        assertEquals(exception, thrownException)
    }

    @Test
    fun `checkEligibility should throw exception when accountsService throws exception`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(25), Gender.M, null, null, false, null, 1)
        val exception = ClientErrorException(clientId, "Accounts", 500)

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenThrow(exception)

        val thrownException = assertThrows<ClientErrorException> {
            eligibilityService.checkEligibility(clientId, correlationId)
        }
        assertEquals(exception, thrownException)
    }

    @Test
    fun `checkEligibility should save attempt record even when not eligible`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val client = Client("123", "Jan", "Kapral", LocalDate.now().minusYears(10), Gender.M, null, null, false, null, 1)
        val accounts = emptyList<Account>()

        `when`(clientsService.getClientById(clientId, correlationId)).thenReturn(client)
        `when`(accountsService.getAccountsByClientId(clientId, correlationId)).thenReturn(accounts)

        val result = eligibilityService.checkEligibility(clientId, correlationId)

        assertFalse(result.eligible)
        verify(eligibilityAttemptRepository).save(any())
    }
}
