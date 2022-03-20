FROM arm64v8/openjdk:11

ARG app_config_path=""

RUN apt-get update && \
    apt-get install -yq --no-install-recommends wget pwgen ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

VOLUME /logs

RUN mkdir /app
WORKDIR /app
COPY . /app
RUN ./gradlew build --no-daemon
CMD ["java", "-Xmx64m", "-Djava.net.preferIPv4Stack=true", "-Des.set.netty.runtime.available.processors=false", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "./build/libs/shadow_app.jar"]