package cz.kapraljan.casestudy.integration

import cz.kapraljan.casestudy.adapters.common.RestTemplateHelper
import cz.kapraljan.casestudy.adapters.customers.models.ClientResponseDto
import cz.kapraljan.casestudy.adapters.customers.models.GenderResponseDto
import cz.kapraljan.casestudy.adapters.accounts.models.*
import cz.kapraljan.casestudy.adapters.db.repositories.EligibilityAttemptRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.util.*
import org.assertj.core.api.Assertions.assertThat

@SpringBootTest
class EligibilityIntegrationTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var eligibilityAttemptRepository: EligibilityAttemptRepository

    @MockitoBean
    private lateinit var restTemplateHelper: RestTemplateHelper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        eligibilityAttemptRepository.deleteAll()
    }

    @Test
    fun `should return eligible when client is adult and has accounts`() {
        val clientId = "eligible-client-123"
        val correlationId = UUID.randomUUID()

        mockClientApiResponse(clientId, correlationId, adultClient())
        mockAccountsApiResponse(clientId, correlationId, accountsWithData())

        val result = mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
                .header("correlation-id", correlationId.toString())
        )

        result
            .andExpect(status().isOk)
            .andExpect(header().string("correlation-id", correlationId.toString()))
            .andExpect(jsonPath("$.eligible").value(true))
            .andExpect(jsonPath("$.reasons").isEmpty)

        val attempts = eligibilityAttemptRepository.findAll()
        assertThat(attempts).hasSize(1)
        assertThat(attempts[0].clientId).isEqualTo(clientId)
        assertThat(attempts[0].eligible).isTrue()
    }

    @Test
    fun `should return ineligible when client is minor`() {
        val clientId = "minor-client-456"
        val correlationId = UUID.randomUUID()

        mockClientApiResponse(clientId, correlationId, minorClient())
        mockAccountsApiResponse(clientId, correlationId, accountsWithData())

        val result = mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
                .header("correlation-id", correlationId.toString())
        )

        result
            .andExpect(status().isOk)
            .andExpect(header().string("correlation-id", correlationId.toString()))
            .andExpect(jsonPath("$.eligible").value(false))
            .andExpect(jsonPath("$.reasons[0]").value("NO_ADULT"))

        val attempts = eligibilityAttemptRepository.findAll()
        assertThat(attempts).hasSize(1)
        assertThat(attempts[0].clientId).isEqualTo(clientId)
        assertThat(attempts[0].eligible).isFalse()
    }

    @Test
    fun `should return ineligible when client has no accounts`() {
        val clientId = "no-accounts-client-789"
        val correlationId = UUID.randomUUID()

        mockClientApiResponse(clientId, correlationId, adultClient())
        mockAccountsApiResponse(clientId, correlationId, noAccountsResponse())

        val result = mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
                .header("correlation-id", correlationId.toString())
        )

        result
            .andExpect(status().isOk)
            .andExpect(header().string("correlation-id", correlationId.toString()))
            .andExpect(jsonPath("$.eligible").value(false))
            .andExpect(jsonPath("$.reasons[0]").value("NO_ACCOUNT"))

        val attempts = eligibilityAttemptRepository.findAll()
        assertThat(attempts).hasSize(1)
        assertThat(attempts[0].clientId).isEqualTo(clientId)
        assertThat(attempts[0].eligible).isFalse()
    }

    @Test
    fun `should return ineligible when client is minor and has no accounts`() {
        val clientId = "ineligible-client-000"
        val correlationId = UUID.randomUUID()

        mockClientApiResponse(clientId, correlationId, minorClient())
        mockAccountsApiResponse(clientId, correlationId, noAccountsResponse())

        val result = mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
                .header("correlation-id", correlationId.toString())
        )

        result
            .andExpect(status().isOk)
            .andExpect(header().string("correlation-id", correlationId.toString()))
            .andExpect(jsonPath("$.eligible").value(false))
            .andExpect(jsonPath("$.reasons").isArray)
            .andExpect(jsonPath("$.reasons[*]").isNotEmpty)

        val attempts = eligibilityAttemptRepository.findAll()
        assertThat(attempts).hasSize(1)
        assertThat(attempts[0].clientId).isEqualTo(clientId)
        assertThat(attempts[0].eligible).isFalse()
    }

    @Test
    fun `should handle requests without correlation-id`() {
        val clientId = "no-correlation-client-111"

        mockClientApiResponse(clientId, null, adultClient())
        mockAccountsApiResponse(clientId, null, accountsWithData())

        val result = mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
        )

        result
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.eligible").value(true))

        val attempts = eligibilityAttemptRepository.findAll()
        assertThat(attempts).hasSize(1)
        assertThat(attempts[0].clientId).isEqualTo(clientId)
        assertThat(attempts[0].eligible).isTrue()
    }

    private fun mockClientApiResponse(clientId: String, correlationId: UUID?, clientResponse: ClientResponseDto) {
        whenever(
            restTemplateHelper.executeRestCall(
                clientId = eq(clientId),
                uri = any(),
                responseType = eq(ClientResponseDto::class.java),
                apiKey = any(),
                serviceName = eq("Customers"),
                correlationId = eq(correlationId)
            )
        ).thenReturn(clientResponse)
    }

    private fun mockAccountsApiResponse(clientId: String, correlationId: UUID?, accountsResponse: GetAccountsResponseDto) {
        whenever(
            restTemplateHelper.executeRestCall(
                clientId = eq(clientId),
                uri = any(),
                responseType = eq(GetAccountsResponseDto::class.java),
                apiKey = any(),
                serviceName = eq("Accounts"),
                correlationId = eq(correlationId)
            )
        ).thenReturn(accountsResponse)
    }

    private fun adultClient(): ClientResponseDto = ClientResponseDto(
        birthDate = LocalDate.now().minusYears(25),
        clientVerificationLevel = 1,
        forename = "John",
        primaryEmail = "john.doe@example.com",
        gender = GenderResponseDto.M,
        primaryPhone = "+420123456789",
        pep = false,
        verifiedBy = "SYSTEM",
        surname = "Doe",
        clientId = "123456789"
    )

    private fun minorClient(): ClientResponseDto = ClientResponseDto(
        birthDate = LocalDate.now().minusYears(10),
        clientVerificationLevel = 1,
        forename = "Jane",
        primaryEmail = "jane.doe@example.com",
        gender = GenderResponseDto.F,
        primaryPhone = "+420987654321",
        pep = false,
        verifiedBy = "SYSTEM",
        surname = "Doe",
        clientId = "987654321"
    )

    private fun accountsWithData(): GetAccountsResponseDto = GetAccountsResponseDto(
        client = ClientDto("test-client", "Test Owner"),
        accounts = listOf(
            NationalAccountDto(
                product_id = "national-acc-1",
                closing_date = Instant.now().plusSeconds(86400),
                prefix = "123",
                number = "4567890123",
                bank_code = "0800"
            ),
            InternationalAccountDto(
                product_id = "international-acc-1",
                closing_date = Instant.now().plusSeconds(86400),
                iban = "CZ6508000000192000145399",
                currency = CurrencyDto.CZK
            )
        )
    )

    private fun noAccountsResponse(): GetAccountsResponseDto = GetAccountsResponseDto(
        client = ClientDto("test-client", "Test Owner"),
        accounts = emptyList()
    )
}
