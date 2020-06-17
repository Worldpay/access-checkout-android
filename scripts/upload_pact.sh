#!/bin/bash
set -e

export SESSIONS_CONTRACT_VERSION="$PROJECT_VERSION"
export VT_CONTRACT_VERSION=$PROJECT_VERSION

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -d@$PROJECT_LOCATION/$LIBRARY_MODULE/target/pacts/access-checkout-android-sdk-verified-tokens.json \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacts/provider/verified-tokens/consumer/access-checkout-android-sdk/version/$VT_CONTRACT_VERSION

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -d@$PROJECT_LOCATION/$LIBRARY_MODULE/target/pacts/access-checkout-android-sdk-sessions.json \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacts/provider/sessions/consumer/access-checkout-android-sdk/version/$SESSIONS_CONTRACT_VERSION
