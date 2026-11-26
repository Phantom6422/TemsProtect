import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    alias(libs.plugins.shadow)
}

defaultTasks("shadowJar")

version = providers.gradleProperty("buildVersion").orElse("26.3").get()
group = "net.temsprotect"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.paper.api)

    implementation(libs.hikari)
    implementation("com.clickhouse:clickhouse-jdbc:${libs.versions.clickhouse.get()}:all") {
        // The "all" classifier already contains the driver and its dependencies.
        isTransitive = false
    }
    compileOnly(libs.duckdb)
    compileOnly(libs.log4j.core)
    compileOnly(libs.log4j.api)
    compileOnly(libs.fastasyncworldedit.core)
    compileOnly(libs.fastasyncworldedit.bukkit) {
        exclude(group = "*", module = "FastAsyncWorldEdit-Core")
    }
    compileOnly(libs.advancedchests)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.paper.api)
    testImplementation(libs.sqlite)
    testImplementation(libs.mysql)
    testImplementation(libs.slf4j.simple)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configurations.configureEach {
    // The ClickHouse driver bundles its own optional dependencies.
    exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(
            "version" to project.version,
            "branch" to (providers.gradleProperty("buildBranch").orElse("release").get()),
            "duckdbVersion" to libs.versions.duckdb.get()
        )
    }
}

tasks.withType<ShadowJar>().configureEach {
    archiveFileName.set("TemsProtect-${project.version}.jar")
    archiveClassifier.set("")

    mergeServiceFiles()
    exclude("**/*.java")
    exclude("**/*.c")
    exclude("module-info.class")
    exclude("META-INF/versions/**/module-info.class")
    exclude("META-INF/maven/**/pom.properties")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")

    relocate("com.zaxxer", "net.coreprotect.libs.hikari")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Multi-Release" to "true",
            "paperweight-mappings-namespace" to "mojang"
        )
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
