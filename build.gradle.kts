import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("io.micrometer:micrometer-tracing-bridge-brave")
	implementation("io.zipkin.reporter2:zipkin-reporter-brave")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configurations {
	all {
		exclude(module = "logback-classic")
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
		exclude(group = "org.slf4j", module = "slf4j-log4j12")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
