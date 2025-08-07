package cz.kapraljan.casestudy.adapters.api

import cz.kapraljan.casestudy.services.EligibilityService
import cz.kapraljan.casestudy.services.LoggingService
import cz.kapraljan.casestudy.services.models.EligibilityResult
import cz.kapraljan.casestudy.exceptions.ClientErrorException
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import java.util.*

@WebMvcTest(
    controllers = [EligibilityController::class],
)
class EligibilityControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var eligibilityService: EligibilityService

    @MockitoBean
    private lateinit var loggingService: LoggingService

    @Test
    fun `getEligibility should return eligibility when service returns success`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()
        val eligibilityResult = EligibilityResult(true, emptyList())

        `when`(eligibilityService.checkEligibility(clientId, correlationId)).thenReturn(eligibilityResult)

        mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
                .header("correlation-id", correlationId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.eligible").value(true))
            .andExpect(jsonPath("$.reasons").isEmpty)
    }

    @Test
    fun `getEligibility should return error response when service throws exception`() {
        val clientId = "123"
        val correlationId = UUID.randomUUID()

        `when`(eligibilityService.checkEligibility(clientId, correlationId))
            .thenThrow(ClientErrorException(clientId, "Test", 400))

        mockMvc.perform(
            get("/api/v1/eligibility")
                .header("clientId", clientId)
                .header("correlation-id", correlationId.toString())
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Požadavek nelze zpracovat"))
            .andExpect(header().string("correlation-id", correlationId.toString()))
    }
}
