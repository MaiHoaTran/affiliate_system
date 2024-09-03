plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.revolut.jooq-docker") version "0.3.12"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.assignment"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

val jwtVersion = "0.12.6"
val mockkVersion = "1.13.12"
val mockitoKotlinVersion = "3.2.0"
val mockitoInlineVersion = "5.2.0"
val hikakuVersion = "3.3.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.postgresql:postgresql")
    jdbc("org.postgresql:postgresql")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jooq:jooq")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.jsonwebtoken:jjwt:$jwtVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoInlineVersion")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testImplementation("de.codecentric.hikaku:hikaku-openapi:$hikakuVersion")
    testImplementation("de.codecentric.hikaku:hikaku-spring:$hikakuVersion")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks {
    generateJooqClasses {
        customizeGenerator {
            database.withForcedTypes(
                org.jooq.meta.jaxb.ForcedType()
                    .withUserType("java.time.Instant")
                    .withIncludeTypes("TIMESTAMP\\ WITH\\ TIME\\ ZONE")
                    .withConverter(
                        """
                            org.jooq.Converter.ofNullable(
                                java.time.OffsetDateTime.class,
                                java.time.Instant.class,
                                java.time.OffsetDateTime::toInstant,
                                instant ->
                                    java.time.OffsetDateTime.ofInstant(
                                        instant, java.time.ZoneId.of("UTC")))
                        """.trimIndent()
                    )
            )
            database.apply { excludes = "^QRTZ_.*" }
        }
    }

    test {
        jvmArgs("--add-opens", "java.base/java.nio=ALL-UNNAMED")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

ktlint {
    // use the version suggested by
    version.set("0.47.1")
    filter {
        // exclude for protocgenerated directories
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
    }
}
