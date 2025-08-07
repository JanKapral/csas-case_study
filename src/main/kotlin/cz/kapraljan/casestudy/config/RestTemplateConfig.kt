package cz.kapraljan.casestudy.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import java.time.Duration

@Configuration
class RestTemplateConfig(
    private val restTemplateLoggingInterceptor: RestTemplateLoggingInterceptor
) {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(30))
            .interceptors(restTemplateLoggingInterceptor)
            .build()
    }
}
