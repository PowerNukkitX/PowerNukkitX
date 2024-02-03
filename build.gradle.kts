import java.nio.charset.StandardCharsets

plugins {
    `java-library`
    `maven-publish`
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
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    api(libs.org.jetbrains.annotations)
    api(libs.com.github.daniellansun.fast.reflection)
    api(libs.net.daporkchop.leveldb.mcpe.jni)
    api(libs.org.cloudburstmc.netty.netty.transport.raknet)
    api(libs.com.nukkitx.natives)
    api(libs.io.netty.netty.transport.classes.epoll)
    api(libs.org.bitbucket.b.c.jose4j)
    api(libs.it.unimi.dsi.fastutil)
    api(libs.com.google.guava.guava)
    api(libs.com.google.code.gson.gson)
    api(libs.org.yaml.snakeyaml)
    api(libs.io.sentry.sentry)
    api(libs.io.sentry.sentry.log4j2)
    api(libs.com.nimbusds.nimbus.jose.jwt)
    api(libs.org.apache.logging.log4j.log4j.core)
    api(libs.org.apache.logging.log4j.log4j.slf4j2.impl)
    api(libs.com.lmax.disruptor)
    api(libs.net.sf.jopt.simple.jopt.simple)
    api(libs.net.minecrell.terminalconsoleappender)
    api(libs.org.jline.jline.terminal)
    api(libs.org.jline.jline.terminal.jna)
    api(libs.org.jline.jline.reader)
    api(libs.com.google.code.findbugs.jsr305)
    api(libs.org.graalvm.sdk.graal.sdk)
    api(libs.org.graalvm.js.js.scriptengine)
    api(libs.cn.powernukkitx.terra.binary)
    api(libs.org.ow2.asm.asm)
    api(libs.com.github.oshi.oshi.core)
    api(libs.cn.powernukkitx.libdeflate.java)
    api(libs.org.lz4.lz4.java)
    api(libs.org.xerial.snappy.snappy.java)
    runtimeOnly(libs.org.graalvm.js.js)
    runtimeOnly(libs.org.graalvm.tools.profiler)
    runtimeOnly(libs.org.graalvm.tools.chromeinspector)
    testImplementation(libs.org.openjdk.jmh.jmh.core)
    testImplementation(libs.org.openjdk.jmh.jmh.generator.annprocess)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.org.mockito.mockito.junit.jupiter)
    testImplementation(libs.fr.inria.gforge.spoon.spoon.core)
    testImplementation(libs.commons.io.commons.io)
    compileOnly(libs.org.projectlombok.lombok)
}

java {
    withSourcesJar()
    withJavadocJar()
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/js", "src/main/resources")
        }
    }
}

tasks.register<DefaultTask>("buildFast"){
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

tasks.register<DefaultTask>("buildSkipChores"){
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

tasks.build {
    dependsOn(tasks.shadowJar)
    group = "alpha build"
}

tasks.clean{
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
}


tasks.withType<AbstractCopyTask>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.shadowJar {
    dependsOn("copyDependencies", tasks.jar)
    manifest {
        attributes(
                "Main-Class" to "cn.nukkit.JarStart"
        )
    }
}

tasks.register<Copy>("copyDependencies") {
    dependsOn(tasks.jar)
    group = "other"
    description = "Copy all dependencies to libs folder"
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("lib"))
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
