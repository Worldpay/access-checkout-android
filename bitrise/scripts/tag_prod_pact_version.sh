#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)

echo "Tagging with PROD..."
pact-broker create-version-tag --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --tag PROD --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_URL -p $PACTBROKER_PASSWORD

echo "Verifying successfully tagged..."
DESCRIPTION=$(pact-broker describe-version --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_URL -p $PACTBROKER_PASSWORD)
if [[ $DESCRIPTION == *"PROD"* ]]
  then
    echo "Successfully tagged as PROD"
  else
    echo "Failed as broker not successfully tagged"
    exit 1
fi
