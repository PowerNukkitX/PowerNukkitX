# This Dockerfile uses Docker Multi-Stage Builds
# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# Requires Docker v17.05

# Use OpenJDK JDK image for intermiediate build
FROM openjdk:21-slim AS build

# Build from source and create artifact
WORKDIR /src

COPY gradlew *.gradle.kts .gitmodules /src/
COPY src /src/src
COPY .git /src/.git
COPY gradle /src/gradle

RUN apt-get clean \
    && apt-get update \
    && apt install git -y
RUN git submodule update --init
RUN ./gradlew shadowJar

# Use OpenJDK JRE image for runtime
FROM openjdk:21-slim AS run

# Copy artifact from build image
COPY --from=build /src/build/powernukkitx-2.0.0-SNAPSHOT-all.jar /app/powernukkitx.jar

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

# Volumes
VOLUME /data /home/minecraft

# Set runtime workdir
WORKDIR /data

# Run app
ENTRYPOINT ["java"]
CMD [ "-Dfile.encoding=UTF-8", "-Djansi.passthrough=true", "-Dterminal.ansi=true", "-XX:+UseZGC", "-XX:+ZGenerational", "-XX:+UseStringDeduplication", "--add-opens","java.base/java.lang=ALL-UNNAMED", "--add-opens","java.base/java.io=ALL-UNNAMED", "--add-opens","java.base/java.net=ALL-UNNAMED", "-cp","/app/powernukkitx.jar:./libs/*", "cn.nukkit.Nukkit" ]
