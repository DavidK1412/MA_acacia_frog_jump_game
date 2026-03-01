plugins {
    id("io.micronaut.application") version "4.6.1"
    id("com.gradleup.shadow") version "8.3.9"
    id("io.micronaut.aot") version "4.6.1"
}

version = "0.1"
group = "com.acacia"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut:micronaut-http-client-jdk")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.sql:micronaut-hibernate-jpa")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")
    
    compileOnly("org.projectlombok:lombok")
    
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


application {
    mainClass = "com.acacia.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.acacia.*")
    }
    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}


