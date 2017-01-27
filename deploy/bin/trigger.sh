#!/bin/bash
#
# Simple deploy script that wraps `vivactl` call.
#
# It basically generate a `deployment.yaml` based on `deploy-macros.m4` and use it as config when calling
# `kubectl`.
#
# Usage:
#
# ./deploy/bin/trigger [ENVIRONMENT] [DB_URL] [VERSION: default(git rev-parse --short HEAD)]
#

environment=${1:-$ENVIRONMENT}
db_url=${2:-$DB_URL}
version=${2:-$VERSION}

version=${version:-$(git rev-parse --short HEAD | tr -d "\n")}

if [[ "$environment" && "$db_url" && "$version" ]]; then
  config_file=deployment-$environment-$version.yaml

  m4 \
    -Denvironment=$environment \
    -Ddb_url=$db_url \
    -Dversion=$version \
    deploy/deploy-macros.m4 deploy/deployment.template.yaml > $config_file

  kubectl apply -f $config_file --record

else
  echo "Not enough args! You must define: environment, db_url and version!"
  echo "Example: ./trigger.sh environment db_url version"
  echo "Or via environment variables: ENVIRONMENT='' DB_URL='' VERSION='' ./trigger.sh"

  exit 1
fi