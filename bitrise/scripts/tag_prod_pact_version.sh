#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacticipants/access-checkout-android-sdk/versions/$PROJECT_VERSION+$HASH_CODE/tags/PROD
