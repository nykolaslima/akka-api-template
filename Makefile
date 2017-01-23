#########
# Tasks #
#########

# Build application (fat jar)
build: dependencies/resources
	$(_sbt-cmd) universal:packageZipTarball

# Build docker image
image: build
	- docker build \
	      --build-arg version=$(version) \
	      --tag $(tag) \
	      --tag $(company_name)/$(project_name) \
	      .

# Start services and third-party dependencies such as postgres, redis, etc
dependencies/services: dependencies/services/run db/migrate
dependencies/services/run:
	- docker-compose up -d

# Stop services and third-party dependencies
dependencies/clean/services:
	- docker-compose stop && docker-compose rm -vf

# Apply migration placed in `/src/main/resources/db/migrations:/flyway/sql` into specified database via
# args `MIGRATE_DB_USER`, `MIGRATE_DB_PASSWORD` and `MIGRATE_DB_URL`:
#
#   make db/migrate MIGRATE_DB_USER="chucknorris" \
#       MIGRATE_DB_PASSWORD="nowthatyouknowyoumustdie" \
#       MIGRATE_DB_URL="jdbc:postgresql://db.expendables.io:5432/jobs"
#
db/migrate:
	$(_flyway_cmd) migrate

# Compile download proto files from `PROTOS_PATH` and output generated classes into `RESOURCES_PATH`
#
#   make dependencies/resources
#
dependencies/resources: dependencies/clean/resources fetch/resources
	- $(_protoc_cmd) scalapbc --proto_path=./$(PROTOS_PATH) \
            --scala_out=flat_package:./$(RESOURCES_PATH) $(shell find "./$(PROTOS_PATH)" -name "*.proto")

# Clean downloaded proto files directory `PROTOS_PATH` and generated classes directory `RESOURCES_PATH`
#
#   make dependencies/clean/resources
#
dependencies/clean/resources:
	- rm -rf $(PROTOS_PATH) $(RESOURCES_PATH)

# Download proto resources from specified Github repository `PROTO_REPOSITORY` and tag `PROTO_VERSION`.
# The downloaded proto files will be placed into `PROTOS_PATH` and it also created the generated
# classes directory `RESOURCES_PATH`.
#
#   make fetch/resources
#
fetch/resources:
	- mkdir -p $(PROTOS_PATH) $(RESOURCES_PATH)
	- git clone \
	        --branch $(PROTO_VERSION) \
	        --depth 1 git@github.com:$(PROTO_REPOSITORY).git \
	        $(PROTOS_PATH) 2> /dev/null

# Setup, run tests and then tear down
#
#   make test
#
test: dependencies/resources dependencies/services test/run dependencies/clean/services

# Compile project with test folder included
#
#   make/compile
#
test/compile: dependencies/resources
	$(_sbt-cmd) test:compile

# Run tests
#
#   make test/run
#
test/run:
	$(_sbt-cmd-with-dependencies) test

# Configure gcloud tool to be used by circleci
#   make circleci/gcloud/setup
#
circleci/gcloud/setup:
	- sudo /opt/google-cloud-sdk/bin/gcloud --quiet components update --version 120.0.0
	- sudo /opt/google-cloud-sdk/bin/gcloud --quiet components update --version 120.0.0 kubectl
	- echo $GCLOUD_SERVICE_KEY | base64 --decode -i > ${HOME}/gcloud-service-key.json
	- sudo /opt/google-cloud-sdk/bin/gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json
	- sudo /opt/google-cloud-sdk/bin/gcloud config set project $PROJECT_NAME
	- sudo /opt/google-cloud-sdk/bin/gcloud --quiet config set container/cluster $CLUSTER_NAME
	- sudo /opt/google-cloud-sdk/bin/gcloud config set compute/zone ${CLOUDSDK_COMPUTE_ZONE}
	- sudo /opt/google-cloud-sdk/bin/gcloud --quiet container clusters get-credentials $CLUSTER_NAME

# Push docker image to gcloud registry
#   make circleci/gcloud/image/publish
#
circleci/gcloud/image/publish: image
	- sudo /opt/google-cloud-sdk/bin/gcloud docker push us.gcr.io/${PROJECT_NAME}/$(version)

# Deploy a new version to GKE cluster
#   make circleci/gcloud/deploy
#
circleci/gcloud/deploy: circleci/gcloud/setup circleci/gcloud/image/publish
	- sudo chown -R ubuntu:ubuntu /home/ubuntu/.kube
	- kubectl patch deployment ${CLUSTER_NAME} -p '{"spec":{"template":{"spec":{"containers":[{"name":"${CLUSTER_NAME}","image":"us.gcr.io/${PROJECT_NAME}/$(version):'"$CIRCLE_SHA1"'"}]}}}}'

###############
# Definitions #
###############

MIGRATE_DB_USER := postgres
MIGRATE_DB_PASSWORD := postgres
MIGRATE_DB_URL := jdbc:postgresql://postgres/akkaapitemplate

PROTO_REPOSITORY = nykolaslima/akka-api-template-resources
PROTO_VERSION = v1.2.0
PROTOS_PATH = tmp/resources
RESOURCES_PATH = src/main/generated-proto

_flyway_cmd = docker run --rm --net host -v ${PWD}/src/main/resources/db/migrations:/flyway/sql \
      shouldbee/flyway \
      -user="$(MIGRATE_DB_USER)" \
      -password="$(MIGRATE_DB_PASSWORD)" \
      -url="$(MIGRATE_DB_URL)"

_protoc_cmd = \
      docker run \
      -v ${PWD}:/target \
      -w /target \
      --rm brennovich/protobuf-tools:latest

company_name = nykolaslima
project_name = akka-api-template
version = $(shell git rev-parse --short HEAD | tr -d "\n")
tag = $(company_name)/$(project_name):$(version)

# Replace `options` with desired value
#
# More details: https://www.gnu.org/software/make/manual/make.html#Substitution-Refs
#
_sbt-cmd = $(_sbt-cmd-base:options=)
_sbt-cmd-with-dependencies = $(_sbt-cmd-base:options=--link postgres:postgres)
_sbt-cmd-base := \
	docker run --rm -it \
		-v $(PWD):/target \
		-v $(HOME)/.ivy2:/root/.ivy2 \
		-v $(HOME)/.m2:/root/.m2 \
		-w /target \
		-e VERSION=$(version) \
		options \
		hseeberger/scala-sbt:latest sbt
