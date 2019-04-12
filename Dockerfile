FROM clojure:lein-alpine as builder
COPY . /tmp/
WORKDIR /tmp
RUN lein uberjar

FROM oracle/graalvm-ce:1.0.0-rc15
RUN gu install R

COPY --from=builder /tmp/target/uberjar/hello-graalvm-0.1.0-SNAPSHOT-standalone.jar /usr/share/java/hello-graalvm-0.1.0-SNAPSHOT-standalone.jar
COPY deploy /usr/local/deploy