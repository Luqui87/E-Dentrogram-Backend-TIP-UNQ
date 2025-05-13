plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	war
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("com.h2database:h2")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation ("org.postgresql:postgresql:42.2.24")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")

	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.mockito:mockito-core")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.security:spring-security-test")

	implementation("com.google.api-client:google-api-client:2.3.0")
	runtimeOnly("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
	implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")
	implementation("com.google.auth:google-auth-library-oauth2-http:1.17.0")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report

	reports {
		xml.required=true
		html.required=true
	}

	classDirectories.setFrom(
		files(
			classDirectories.files.map {
				fileTree(it) {
					exclude(
						"com/example/E_Dentogram/EDentogramApplication.kt", // main method class
						"com/example/E_Dentogram/ServletInitializer.kt"
					)
				}
			}
		)
	)
}







