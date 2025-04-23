import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

plugins {
    `java-library`
    `maven-publish`
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
            srcDirs("src/main/resources")
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
    delete("pnx.yml", "terra", "services")
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xpkginfo:always")
    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED"))
    jvmArgs(listOf("--add-opens", "java.base/java.io=ALL-UNNAMED"))
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    reports {
        csv.required = false
        xml.required = true
        html.required = false
    }
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<AbstractCopyTask>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<AbstractArchiveTask>("sourcesJar") {
    destinationDirectory = layout.buildDirectory
}

tasks.jar {
    destinationDirectory = layout.buildDirectory
    doLast {//execution phase
        val f: RegularFile = archiveFile.get()
        val tf: RegularFile = layout.buildDirectory.file("${project.description}.jar").get()
        Files.copy(Path.of(f.asFile.absolutePath), Path.of(tf.asFile.absolutePath), StandardCopyOption.REPLACE_EXISTING)
    }
}

tasks.shadowJar {
    dependsOn("copyDependencies")
    manifest {
        attributes(
            "Main-Class" to "cn.nukkit.JarStart"
        )
    }

    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java) //required to fix shadowJar log4j2 issue

    destinationDirectory = layout.buildDirectory
}

tasks.register<Copy>("copyDependencies") {
    dependsOn(tasks.jar)
    group = "other"
    description = "Copy all dependencies to libs folder"
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs"))
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

    repositories {
        maven {
            url = uri("https://repo.powernukkitx.org/releases")
            credentials {
                username = findProperty("pnxUsername") as String? ?: System.getenv("PNX_REPO_USERNAME")
                password = findProperty("pnxPassword") as String? ?: System.getenv("PNX_REPO_PASSWORD")
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
