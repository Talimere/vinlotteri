plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.talimere"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
	implementation(libs.bundles.spring)

	runtimeOnly(libs.bundles.runtime)

	testImplementation(libs.bundles.testing)
	mockitoAgent(libs.mockito) { isTransitive = false }

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()

	jvmArgs = (jvmArgs ?: mutableListOf()).apply { add("-javaagent:${mockitoAgent.asPath}") }

	testLogging {
		events("passed", "failed")
	}
}

tasks.test {
	useJUnitPlatform {
		includeTags("unit")
	}
}

val integrationTest by tasks.registering(Test::class) {
	description = "Runs integration tests."
	group = "verification"

	testClassesDirs = sourceSets["test"].output.classesDirs
	classpath = sourceSets["test"].runtimeClasspath

	useJUnitPlatform {
		includeTags("integration")
	}

	systemProperty("testcontainers.reuse.enable", "true")

	shouldRunAfter(tasks.test)
}
