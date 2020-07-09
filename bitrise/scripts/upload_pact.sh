#!/bin/bash
set -e

export HASH_CODE=$(git rev-parse --short HEAD)
export BRANCH=$(git rev-parse --abbrev-ref HEAD)

export PROJECT_VERSION="$PROJECT_VERSION"

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -d@$LIBRARY_MODULE/target/pacts/access-checkout-android-sdk-verified-tokens.json \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacts/provider/verified-tokens/consumer/access-checkout-android-sdk/version/$PROJECT_VERSION+$HASH_CODE

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -d@$LIBRARY_MODULE/target/pacts/access-checkout-android-sdk-sessions.json \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacts/provider/sessions/consumer/access-checkout-android-sdk/version/$PROJECT_VERSION+$HASH_CODE


if [ $BRANCH == "master" ]
then
  curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacticipants/access-checkout-android-sdk/versions/$PROJECT_VERSION+$HASH_CODE/tags/master
fi
