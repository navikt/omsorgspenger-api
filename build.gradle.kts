import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java {
    sourceCompatibility = JavaVersion.VERSION_11
}

extra["springCloudVersion"] = "Hoxton.SR1"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val springSecurityVersion = "5.2.1.RELEASE"

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // See here for more info about configuring security: https://docs.spring.io/spring-security/site/docs/current/reference/html/webflux-oauth2.html#webflux-oauth2-resource-server
    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-resource-server
    implementation("org.springframework.security:spring-security-oauth2-resource-server:$springSecurityVersion")

    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-jose
    implementation("org.springframework.security:spring-security-oauth2-jose:$springSecurityVersion")

    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-config
    implementation("org.springframework.security:spring-security-config:$springSecurityVersion")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-sleuth
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

    // https://mvnrepository.com/artifact/io.projectreactor.addons/reactor-extra
    implementation("io.projectreactor.addons:reactor-extra:3.3.1.RELEASE")

    // https://mvnrepository.com/artifact/net.logstash.logback/logstash-logback-encoder
    implementation("net.logstash.logback:logstash-logback-encoder:6.2")
    implementation("io.micrometer:micrometer-core:1.3.1")
    implementation("io.micrometer:micrometer-registry-prometheus:1.3.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.2.26")
    implementation("org.springdoc:springdoc-openapi-security:1.2.26")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:1.1.3")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-contract-wiremock
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:2.2.0.RELEASE")

    compile ("io.lettuce:lettuce-core:5.2.1.RELEASE")
    implementation("com.github.fppt:jedis-mock:0.1.16")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}