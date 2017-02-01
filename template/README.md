# Akka API Template

This application aim to provide a sample application implementing a REST API with Akka technology.  
The endpoints support JSON and binary Protobuf protocols with Swagger as documentation tool.  


- Tools
  - [Circle CI/Deploy](https://circleci.com/gh/$company$/$name$),
  - [Container Registry](https://console.cloud.google.com/kubernetes/images/tags/api?location=US&project=$name$&authuser=1),
  - Swagger: [QA](http://localhost:9090/api-docs/)

## Requirements

- [Docker](https://docs.docker.com/engine/installation/)

## Makefile

We use a handful `Makefile` that knows how to compile, build, test and publish the application into a docker
image. The whole idea to use `make` aims into the premise of providing almost-zero-setup requirement to run
day-to-day task when developing and deploying an application.

### Tasks

- `make build`: Build a self-contained jar with all dependencies included.
- `make image`: Build a Docker image with the latest tag (implicates `build`).
- `make image/publish`: Publishes the built image (implicates `build` and `image`).
- `make dependencies/resources`: Download the [Protobuf files](https://github.com/$company$/$name$-resources) and generate the Scala classes that will be used by the project. (Please look at [Makefile](https://github.com/$company$/$name$/blob/master/Makefile) in order to configure `proto_version`)
- `male dependencies/clean/resources`: Clean the downloaded Protobuf files.
- `make test/compile`: Compile application with test dependencies.
- `make test/run`: Run tests (implicates `test/compile`).
- `make test`: All-in-one command to start requirements, compile and test the application.

## Deployment

### Pipeline

New images are generated via make tasks in Cicle CI (look circle.yml), deployment pipelines are triggered if
build is successful. Our deploy is powered by Circle CI Google cloudand the steps
used to create our environments are described bellow:

#### Continuous Deployment

After bootstrapping we can benefit from our continuous deployment setup which is described below:

```
Github's Pull Request ->
Merge ->
Circle (here docker image is built, and on merge in master deploy is triggered) ->
QA Deployment via gcloud+kubectl
```


## Development

### Running the application

The docker image defines an entrypoint ready to run and optionally pass custom args:

```sh
docker run \
  -p 8080:8080 \
  -e environment=qa \
  $company$/$name$:latest \
  -Dakka.loglevel=WARN
```
