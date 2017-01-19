FROM java:8-jre-alpine

ARG version

COPY ./target/universal/akka-api-${version}.tgz /usr/local/app/akka-api-${version}.tgz

WORKDIR /usr/local/app

RUN tar xf akka-api-${version}.tgz
RUN rm akka-api-${version}.tgz

WORKDIR /usr/local/app/akka-api-${version}

ENTRYPOINT ["./bin/akka-api"]
