FROM java:8-jre-alpine

ARG version

RUN apk add --update \
    bash \
    && rm     -rf /var/cache/apk/*

COPY ./target/universal/$name$-${version}.tgz /usr/local/app/$name$-${version}.tgz

WORKDIR /usr/local/app

RUN tar xf $name$-${version}.tgz
RUN rm $name$-${version}.tgz

WORKDIR /usr/local/app/$name$-${version}

EXPOSE 8080

ENTRYPOINT ["./bin/$name$"]

