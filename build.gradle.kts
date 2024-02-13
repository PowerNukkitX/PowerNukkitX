import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

plugins {
    `java-library`
    `maven-publish`
    idea
    jacoco
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

group = "cn.powernukkitx"
version = "2.0.0-SNAPSHOT"
description = "powernukkitx"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    api(libs.bundles.netty)
    api(libs.bundles.logging)
    api(libs.annotations)
    api(libs.jsr305)
    api(libs.gson)
    api(libs.guava)
    api(libs.commonsio)
    api(libs.fastutil)
    api(libs.leveldbjni)
    api(libs.snakeyaml)
    api(libs.stateless4j)


    compileOnly(libs.lombok)
    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.rng.simple)
    implementation(libs.rng.sampling)
    implementation(libs.asm)
    implementation(libs.jose4j)
    implementation(libs.joptsimple)
    implementation(libs.sentry)
    implementation(libs.sentry.log4j2)
    implementation(libs.disruptor)
    implementation(libs.oshi)
    implementation(libs.fastreflection)
    implementation(libs.terra)
    implementation(libs.bundles.compress)
    implementation(libs.bundles.terminal)
    implementation(libs.bundles.graalvm)
    runtimeOnly(libs.bundles.graalvm.runtime)

    testImplementation(libs.bundles.test)
    testImplementation(libs.commonsio)
    testImplementation(libs.commonslang3)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

java {
    withSourcesJar()
    withJavadocJar()
}

//Automatically download dependencies source code
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = false
    }
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/js", "src/main/resources")
        }
    }
}

tasks.register<DefaultTask>("buildFast") {
    dependsOn(tasks.build)
    group = "alpha build"
    tasks["delombok"].enabled = false
    tasks["javadoc"].enabled = false
    tasks["javadocJar"].enabled = false
    tasks["sourcesJar"].enabled = false
    tasks["copyDependencies"].enabled = false
    tasks["shadowJar"].enabled = false
    tasks["compileTestJava"].enabled = false
    tasks["processTestResources"].enabled = false
    tasks["testClasses"].enabled = false
    tasks["test"].enabled = false
    tasks["check"].enabled = false
}

tasks.register<DefaultTask>("buildSkipChores") {
    dependsOn(tasks.build)
    group = "alpha build"
    tasks["delombok"].enabled = false
    tasks["javadoc"].enabled = false
    tasks["javadocJar"].enabled = false
    tasks["sourcesJar"].enabled = false
    tasks["compileTestJava"].enabled = false
    tasks["processTestResources"].enabled = false
    tasks["testClasses"].enabled = false
    tasks["test"].enabled = false
    tasks["check"].enabled = false
}

tasks.register<DefaultTask>("buildForGithubAction") {
    dependsOn(tasks.build)
    group = "build"
    tasks["delombok"].enabled = false
    tasks["javadoc"].enabled = false
    tasks["javadocJar"].enabled = false
}

tasks.build {
    dependsOn(tasks.shadowJar)
    group = "alpha build"
}

tasks.clean {
    group = "alpha build"
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xpkginfo:always")
    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED"))
    jvmArgs(listOf("--add-opens", "java.base/java.io=ALL-UNNAMED"))
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    reports {
        csv.required = true
        xml.required = false
        html.required = false
    }
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<AbstractCopyTask>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<AbstractArchiveTask>("sourcesJar") {
    dependsOn("copySomeFile")
    destinationDirectory = layout.buildDirectory
}

tasks.compileTestJava {
    dependsOn("copySomeFile")
}

tasks.jar {
    destinationDirectory = layout.buildDirectory
    finalizedBy("copySomeFile")
    doLast {//execution phase
        project.extra["jarfile"] = archiveFile.get()
    }
}

tasks.register<Copy>("copySomeFile") {
    dependsOn("jar")
    from(project.projectDir.resolve("scripts"))
    into(layout.buildDirectory)
    //Keep track of incremental update, and if it does, the copySomeFile task need to be updated
    if (tasks.jar.get().isEnabled) {
        // Input files are added only if the jar task exists
        inputs.files(tasks.jar)
    }
    doLast {//execution phase
        if (project.extra.has("jarfile")) {
            val f: RegularFile = project.extra["jarfile"] as RegularFile
            val tf: RegularFile = layout.buildDirectory.file("${project.description}.jar").get()
            Files.copy(Path.of(f.asFile.absolutePath), Path.of(tf.asFile.absolutePath), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

tasks.shadowJar {
    dependsOn("copyDependencies", "copySomeFile")
    manifest {
        attributes(
                "Main-Class" to "cn.nukkit.JarStart"
        )
    }
    destinationDirectory = layout.buildDirectory
}

tasks.register<Copy>("copyDependencies") {
    dependsOn(tasks.jar)
    group = "other"
    description = "Copy all dependencies to libs folder"
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs"))
}

tasks.delombok {
    dependsOn("copySomeFile")
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
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
