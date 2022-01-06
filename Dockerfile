FROM arm64v8/openjdk:11
RUN apt-get update && \
    apt-get install -yq --no-install-recommends wget pwgen ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*


RUN mkdir /app
WORKDIR /app
COPY . /app
RUN ./gradlew build --no-daemon
CMD ["java",
#"-DppConfig=/application-bot.yaml",
"-Xmx128m",
#"-Djava.awt.headless=true",
#"-Dorg.bytedeco.javacpp.maxbytes=1100m",
#"-Dorg.bytedeco.javacpp.maxphysicalbytes=1800m" ,
 "-Djava.net.preferIPv4Stack=true",
 "-Des.set.netty.runtime.available.processors=false",
 "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
 "-jar", "/app.jar"]