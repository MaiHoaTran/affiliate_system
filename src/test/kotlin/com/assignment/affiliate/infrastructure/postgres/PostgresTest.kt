package com.assignment.affiliate.infrastructure.postgres

import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.test.context.ActiveProfiles

@JooqTest(properties = ["spring.flyway.enabled=true"])
@ActiveProfiles("test")
annotation class PostgresTest()
