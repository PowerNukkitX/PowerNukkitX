import java.nio.charset.StandardCharsets
// Explicit Gradle API imports to fix Kotlin DSL unresolved references
import org.gradle.external.javadoc.CoreJavadocOptions
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.publish.maven.MavenPublication
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    `java-library`
    `maven-publish`
    java
    idea
    jacoco
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.freefair.lombok") version "8.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

group = "org.powernukkitx"
version = "2.0.0-SNAPSHOT"
description = "PNX Server"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

// Constants
val SHADOW_JAR = "shadowJar"

dependencies {
    api(libs.bundles.netty)
    api(libs.bundles.logging)
    api(libs.annotations)
    api(libs.jsr305)
    api(libs.gson)
    api(libs.guava)
    api(libs.commonsio)
    api(libs.fastutil)
    api(libs.snakeyaml)
    api(libs.stateless4j)

    implementation(libs.bundles.leveldb)
    implementation(libs.rng.simple)
    implementation(libs.rng.sampling)
    implementation(libs.asm)
    implementation(libs.jose4j)
    implementation(libs.joptsimple)
    implementation(libs.disruptor)
    implementation(libs.oshi)
    implementation(libs.fastreflection)
    implementation(libs.terra)
    implementation(libs.bundles.compress)
    implementation(libs.bundles.terminal)
    implementation(libs.okaeri)

    testImplementation(libs.bundles.test)
    testImplementation(libs.commonsio)
    testImplementation(libs.commonslang3)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

configurations.all {
    resolutionStrategy {
        cacheDynamicVersionsFor(10, "minutes")
        cacheChangingModulesFor(10, "minutes")
        preferProjectModules()
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.annotationProcessorPath = configurations.getByName("annotationProcessor")
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

//Automatically download dependencies source code
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = false
        excludeDirs.addAll(listOf(
            file(".gradle"),
            file("build"),
            file("out")
        ))
    }
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<DefaultTask>("buildFast") {
    group = "alpha build"
    description = "Fast build without documentation and tests - for rapid development"
    dependsOn(tasks.compileJava, tasks.processResources, tasks.classes, tasks.jar)
}

tasks.register<DefaultTask>("buildSkipChores") {
    group = "alpha build"
    description = "Build without documentation and tests"
    dependsOn(tasks.compileJava, tasks.processResources, tasks.classes, tasks.jar, SHADOW_JAR)
}

tasks.register<DefaultTask>("buildForGithubAction") {
    group = "build"
    description = "Optimized build for CI/CD pipelines (without tests)"
    dependsOn(tasks.compileJava, tasks.processResources, tasks.classes, tasks.jar, SHADOW_JAR)
}

tasks.build {
    dependsOn(SHADOW_JAR)
    group = "alpha build"
}

tasks.clean {
    group = "alpha build"
    description = "Deletes the build directory and generated files"
    delete("pnx.yml", "terra", "services")
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
        "-Xpkginfo:always",
        "-parameters",
        "-Xlint:-options",
        "-Xlint:deprecation",
        "-Xlint:unchecked"
    ))
    options.isIncremental = true
    options.isFork = true
    options.forkOptions.jvmArgs = listOf("-Xmx2g")
    options.release.set(21)

    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.isFork = true
    options.forkOptions.jvmArgs = listOf("-Xmx1g")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.io=ALL-UNNAMED",
        "-Xmx1g", // Limit test JVM memory
        "-XX:+UseG1GC", // Use G1GC for tests
        "-XX:MaxGCPauseMillis=200" // Lower GC pause time
    )

    // Performance for tests
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    forkEvery = 100 // Fork new JVM after 100 tests

    // Test settings
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = false
    }

    finalizedBy("jacocoTestReport") // report is always generated after tests run
}


tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        csv.required = false
        xml.required = true
        html.required = false
    }
    dependsOn("test") // tests are required to run before generating the report
}

tasks.withType<AbstractCopyTask>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<AbstractArchiveTask>("sourcesJar") {
    destinationDirectory.set(layout.buildDirectory)
}

// Improve build reproducibility for better caching
tasks.withType<AbstractArchiveTask> {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    destinationDirectory.set(layout.buildDirectory)
    archiveFileName.set("${project.description}.jar")
}

tasks.named<ShadowJar>("shadowJar") {
    dependsOn("copyDependencies")
    archiveClassifier.set("shaded")

    manifest {
        attributes(
            "Main-Class" to "cn.nukkit.JarStart",
            "Implementation-Version" to project.version,
            "Implementation-Title" to project.name,
            "Multi-Release" to "true"
        )
    }

    // Required to fix shadowJar log4j2 plugin caching issue
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)

    // Minimize JAR size by excluding unnecessary files
    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE*",
        "META-INF/NOTICE*",
        "META-INF/maven/**",
        "about.html"
    )

    // Merge service files for better compatibility
    mergeServiceFiles()

    destinationDirectory.set(layout.buildDirectory)

    // Enable ZIP64 format for large archives (>4GB)
    isZip64 = true
}

tasks.register<Copy>("copyDependencies") {
    dependsOn(tasks.jar)
    group = "other"
    description = "Copy all dependencies to libs folder"
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs"))

    // Enable up-to-date checking for better incremental builds
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // Performance: Only copy if dependencies changed
    inputs.files(configurations.runtimeClasspath)
    outputs.dir(layout.buildDirectory.dir("libs"))
}

tasks.javadoc {
    options.encoding = StandardCharsets.UTF_8.name()
    includes.add("**/**.java")
    val javadocOptions = options as CoreJavadocOptions
    javadocOptions.addStringOption(
        "source",
        java.sourceCompatibility.toString()
    )
    // Suppress some meaningless warnings
    javadocOptions.addStringOption("Xdoclint:none", "-quiet")

    // Performance: Only generate javadoc for public API
    javadocOptions.addBooleanOption("public", true)

    // Enable parallel processing
    isFailOnError = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "server"
            pom {
                url.set("https://github.com/PowerNukkitX/PowerNukkitX")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/PowerNukkitX/PowerNukkitX.git")
                    developerConnection.set("scm:git:ssh://github.com/PowerNukkitX/PowerNukkitX.git")
                    url.set("https://github.com/PowerNukkitX/PowerNukkitX")
                }
            }
        }
    }

    repositories {
        maven {
            name = "pnx"
            url = uri("https://repo.powernukkitx.org/releases")
            credentials {
                username = providers.gradleProperty("pnxUsername")
                    .orElse(providers.environmentVariable("PNX_REPO_USERNAME"))
                    .orNull
                password = providers.gradleProperty("pnxPassword")
                    .orElse(providers.environmentVariable("PNX_REPO_PASSWORD"))
                    .orNull
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

// Task optimization - disable unnecessary tasks for faster builds
tasks.configureEach {
    // Skip tasks that aren't needed for standard builds
    if (name.contains("delombok") && !gradle.startParameter.taskNames.contains("javadoc")) {
        enabled = false
    }
}