import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

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

    // See here for more info about configuring security: https://docs.spring.io/spring-security/site/docs/current/reference/html/webflux-oauth2.html#webflux-oauth2-resource-server
    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-resource-server
    implementation("org.springframework.security:spring-security-oauth2-resource-server:$springSecurityVersion")

    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-jose
    implementation("org.springframework.security:spring-security-oauth2-jose:$springSecurityVersion")

    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-config
    implementation("org.springframework.security:spring-security-config:$springSecurityVersion")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-sleuth
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:2.1.6.RELEASE")

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