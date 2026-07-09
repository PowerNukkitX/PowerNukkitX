# This Dockerfile uses Docker Multi-Stage Builds
# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# Requires Docker v17.05

# Use Temurin JDK image for intermediate build
FROM eclipse-temurin:21-jdk AS build

# Build from source and create artifact
WORKDIR /src

COPY gradlew gradlew.bat *.gradle.kts gradle.properties .gitmodules /src/
COPY src /src/src
# generateGitProperties and git submodule setup need git metadata during image builds.
COPY .git /src/.git
COPY gradle /src/gradle

RUN apt-get update \
    && apt-get install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*
RUN git submodule update --init
RUN ./gradlew shadowJar --no-daemon --no-configuration-cache

# Use Temurin JDK image for runtime. Some server/plugin paths expect JDK tooling.
FROM eclipse-temurin:21-jdk AS run

# Copy artifact from build image
COPY --from=build /src/build/powernukkitx.jar /app/powernukkitx.jar

# Docker starts without an interactive terminal, so setup must not block.
ENV PNX_SETUP_NON_INTERACTIVE=true

# Create minecraft user
RUN useradd --user-group \
            --no-create-home \
            --home-dir /data \
            --shell /usr/sbin/nologin \
            minecraft

# Ports
EXPOSE 19132

RUN mkdir /data && mkdir /home/minecraft
RUN chown -R minecraft:minecraft /app /data /home/minecraft

# User and group to run as
USER minecraft:minecraft

# Volumes:
# /data stores server configuration, worlds, plugins, and logs.
# /home/minecraft stores per-user Java/JLine cache files.
VOLUME /data /home/minecraft

# Set runtime workdir
WORKDIR /data

# Run app
ENTRYPOINT ["java"]
CMD [ "-Dfile.encoding=UTF-8", "-Djansi.passthrough=true", "-Dterminal.ansi=true", "-XX:+UseZGC", "-XX:+ZGenerational", "-XX:+UseStringDeduplication", "--add-opens","java.base/java.lang=ALL-UNNAMED", "--add-opens","java.base/java.io=ALL-UNNAMED", "--add-opens","java.base/java.net=ALL-UNNAMED", "-cp","/app/powernukkitx.jar:./libs/*", "org.powernukkitx.PowerNukkitX" ]
