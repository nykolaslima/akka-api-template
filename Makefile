.PHONY = \
	db/migrate \
	dependencies/services/up

#########
# Tasks #
#########

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

MIGRATE_DB_USER := postgres
MIGRATE_DB_PASSWORD := postgres
MIGRATE_DB_URL := jdbc:postgresql://postgres/akkaapitemplate

_flyway_cmd = docker run --rm --net host -v ${PWD}/src/main/resources/db/migrations:/flyway/sql \
      shouldbee/flyway \
      -user="$(MIGRATE_DB_USER)" \
      -password="$(MIGRATE_DB_PASSWORD)" \
      -url="$(MIGRATE_DB_URL)"
