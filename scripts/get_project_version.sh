#!/bin/bash
set -e

ALLOW_SNAPSHOTS=false

if [[ "$1" == "--allow-snapshots" ]]; then
  ALLOW_SNAPSHOTS=true
fi

VERSION=$($PROJECT_LOCATION/gradlew -b $PROJECT_LOCATION/build.gradle :$LIBRARY_MODULE:properties \
  --no-daemon --console=plain -q | grep "^version:" | awk '{printf $2}')


if [[ -z ${VERSION} ]] || [[ ${VERSION} = "unspecified" ]]; then
  echo "Project version was not set!"
  exit 1
fi

if [[ ${ALLOW_SNAPSHOTS} = false ]] && [[ ${VERSION} == *"-SNAPSHOT"* ]]; then
  echo "Project had incorrect version set - not allowed to be empty or a SNAPSHOT: $VERSION"
  exit 1
fi


envman add --key "PROJECT_VERSION" --value "$VERSION"
