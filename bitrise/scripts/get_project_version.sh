#!/bin/bash
set -e

ALLOW_SNAPSHOTS=false

if [[ "$1" == "--allow-snapshots" ]]; then
  ALLOW_SNAPSHOTS=true
fi

VERSION=$(./gradlew -b build.gradle :"${LIBRARY_MODULE}":properties \
  --no-daemon --console=plain -q | grep "^version:" | awk '{printf $2}')


if [[ -z ${VERSION} ]] || [[ ${VERSION} = "unspecified" ]]; then
  echo "The project version was not set!"
  exit 1
fi

if [[ ${ALLOW_SNAPSHOTS} = false ]] && [[ ${VERSION} == *"-SNAPSHOT"* ]]; then
  echo "The project had an incorrect version set - it is not allowed to be a SNAPSHOT version: $VERSION"
  exit 1
fi


envman add --key "PROJECT_VERSION" --value "$VERSION"
