#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)

pact-broker create-version-tag --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --tag PROD --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_URL -p $PACTBROKER_PASSWORD
