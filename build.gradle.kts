plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	idea
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

val querydslVersion = "5.0.0"

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	implementation("org.springframework.retry:spring-retry")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	// DB
	runtimeOnly("com.mysql:mysql-connector-j")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	// QueryDSL
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api:3.1.0")

	// Spring Validator
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.redisson:redisson-spring-boot-starter:3.43.0")
	implementation("com.fasterxml.jackson.core:jackson-databind")

    // Kafka
    implementation ("org.springframework.kafka:spring-kafka:2.8.10")
    testImplementation ("org.springframework.kafka:spring-kafka-test")
}

val querydslDir = "${project.buildDir}/generated/sources/annotationProcessor/java/main"

sourceSets {
	main {
		java {
			srcDir(querydslDir)
		}
	}
}

tasks.register("compileQuerydsl", JavaCompile::class) {
	source = fileTree("src/main/java")
	classpath = configurations["annotationProcessor"] + configurations["implementation"]
	destinationDirectory.set(file(querydslDir))
	options.annotationProcessorPath = configurations["annotationProcessor"]
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}
