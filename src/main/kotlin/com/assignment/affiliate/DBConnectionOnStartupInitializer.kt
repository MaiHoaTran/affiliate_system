package com.assignment.affiliate

import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class DBConnectionOnStartupInitializer(private val dslContext: DSLContext) :
    ApplicationListener<ApplicationReadyEvent> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("Initializing a DB connection...")
        try {
            dslContext.selectOne().fetch()
            logger.info("DB connection is initialized")
        } catch (exception: Exception) {
            logger.error(
                "Could not initialize a DB connection on startup, marking application as 'ready' regardless",
                exception
            )
        }
    }
}
