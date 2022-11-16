FROM alpine/git:v2.36.3 AS prepare

# Copy the source to maven build
WORKDIR /work

COPY pom.xml /work
COPY src/ /work/src
COPY .git /work/.git
COPY entrypoint.sh /work/entrypoint.sh

# Update the language submodule
RUN if [ -z "$(ls -A /work/src/main/resources/language)" ]; then git submodule update --init; fi


FROM maven:3.8.6-eclipse-temurin-17-alpine as build

WORKDIR /build

COPY --from=prepare /work /build
# cache maven dependency
RUN mvn verify --fail-never
# Build the source
RUN mvn -Dmaven.javadoc.skip=true -Dfile.encoding=UTF-8 package -P dev

# Final image
FROM findepi/graalvm:java17
LABEL author="CoolLoong"
WORKDIR /pnx

ENV Xmx=2G
ENV Xms=1G

COPY --from=build /build/entrypoint.sh /pnx/entrypoint.sh
COPY --from=build /build/target/libs /pnx/libs
COPY --from=build /build/target/powernukkitx-1.19.40-r3.jar /pnx/powernukkitx.jar

RUN gu install js && \
    mkdir -p /pnx/plugins /pnx/players /pnx/worlds && \
    chmod +x /pnx/entrypoint.sh

EXPOSE 19132/udp
VOLUME ["/pnx/plugins","/pnx/players","/pnx/worlds"]
ENTRYPOINT ["/pnx/entrypoint.sh"]
CMD ["$Xms","$Xmx"]