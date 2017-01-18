.PHONY = \
	db/migrate \
	dependencies/services/up

#########
# Tasks #
#########

# Start services and third-party dependencies such as postgres, redis, etc
dependencies/services/up: dependencies/services/run db/migrate
dependencies/services/run:
	- docker-compose up -d

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

###############
# Definitions #
###############

MIGRATE_DB_USER := postgres
MIGRATE_DB_PASSWORD := postgres
MIGRATE_DB_URL := jdbc:postgresql://postgres/akkaapitemplate

PROTO_REPOSITORY = nykolaslima/akka-api-template-resources
PROTO_VERSION = v1.0.0
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
