FROM java:8-jre-alpine

ARG version

RUN apk add --update \
	    bash \
	    && rm -rf /var/cache/apk/*

COPY ./target/universal/akka-api-template-${version}.tgz /usr/local/app/akka-api-template-${version}.tgz

WORKDIR /usr/local/app

RUN tar xf akka-api-template-${version}.tgz
RUN rm akka-api-template-${version}.tgz

WORKDIR /usr/local/app/akka-api-template-${version}

ENTRYPOINT ["./bin/akka-api-template"]
