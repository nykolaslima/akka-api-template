.PHONY = \
	db/migrate \
	dependencies/services/up \
	image \
	image/publish

#########
# Tasks #
#########

# Build application
build:
	VERSION=$(version) sbt clean compile universal:packageZipTarball

# Build docker image
image: build
	- docker build \
	      --build-arg version=$(version) \
	      --tag $(tag) \
	      --tag $(company_name)/$(project_name) \
	      .

# Push docker image
image/publish: image
	- docker push $(tag)
	- docker push $(company_name)/$(project_name)

# Start services and third-party dependencies such as postgres, redis, etc
dependencies/services/up:
	docker-compose up -d

# Apply migration placed in `/src/main/resources/db/migrations:/flyway/sql` into specified database via
# args `MIGRATE_DB_USER`, `MIGRATE_DB_PASSWORD` and `MIGRATE_DB_URL`:
#
#   make db/migrate MIGRATE_DB_USER="chucknorris" \
#       MIGRATE_DB_PASSWORD="nowthatyouknowyoumustdie" \
#       MIGRATE_DB_URL="jdbc:postgresql://db.expendables.io:5432/jobs"
#
db/migrate:
	$(_flyway_cmd) migrate

###############
# Definitions #
###############

company_name = com.templates
project_name = akka-api
version = $(shell git rev-parse --short HEAD | tr -d "\n")
tag = $(company_name)/$(project_name):$(version)

MIGRATE_DB_USER := postgres
MIGRATE_DB_PASSWORD := postgres
MIGRATE_DB_URL := jdbc:postgresql://postgres/akkaapitemplate

_flyway_cmd = docker run --rm --net host -v ${PWD}/src/main/resources/db/migrations:/flyway/sql \
      shouldbee/flyway \
      -user="$(MIGRATE_DB_USER)" \
      -password="$(MIGRATE_DB_PASSWORD)" \
      -url="$(MIGRATE_DB_URL)"
