plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.opencollab.dev/maven-releases/")
    }

    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
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
    annotationProcessor(libs.org.projectlombok.lombok)
}

group = "cn.powernukkitx"
version = "2.0.0-SNAPSHOT"
description = "powernukkitx"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    withSourcesJar()
    withJavadocJar()
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

tasks.jar {
    manifest {
        attributes["Main-Class"] = "cn.nukkit.JarStart"
    }

    destinationDirectory.set(file("out"))
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "cn.nukkit.JarStart"
    }

    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
    destinationDirectory.set(file("out"))
}