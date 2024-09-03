package com.assignment.affiliate.api.http

import com.assignment.affiliate.TestConfig
import de.codecentric.hikaku.Hikaku
import de.codecentric.hikaku.HikakuConfig
import de.codecentric.hikaku.converters.openapi.OpenApiConverter
import de.codecentric.hikaku.converters.spring.SpringConverter
import de.codecentric.hikaku.endpoints.HttpMethod
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.nio.file.Paths

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig::class)
internal class SpecificationTest(@Autowired private val springContext: ApplicationContext) {
    @Test
    internal fun `specification matches implementation`() {
        val ignorePaths = setOf(SpringConverter.IGNORE_ERROR_ENDPOINT, "/api/v1/users/jwt")
        val filters =
            listOf(
                { endpoint -> endpoint.httpMethod == HttpMethod.HEAD },
                { endpoint -> endpoint.httpMethod == HttpMethod.OPTIONS },
                { endpoint -> ignorePaths.contains(endpoint.path) },
                SpringConverter.IGNORE_ERROR_ENDPOINT
            )

        Hikaku(
            specification = OpenApiConverter(Paths.get("openapi.yaml")),
            implementation = SpringConverter(springContext),
            config = HikakuConfig(filters = filters)
        ).match()
    }

    @TestConfiguration
    class Config {
        /**
         * Workaround for Hikaku's `applicationContext.getBean(RequestMappingHandlerMapping::class.java)`
         * which causes test to fail with `NoUniqueBeanDefinitionException`, because of
         * [this change](https://github.com/spring-projects/spring-boot/issues/29682) in
         * SpringBoot 2.7.x.
         */
        @Bean
        @Primary
        fun primaryRequestMappingHandlerMapping(
            requestMappingHandlerMapping: RequestMappingHandlerMapping
        ) =
            requestMappingHandlerMapping
    }
}
