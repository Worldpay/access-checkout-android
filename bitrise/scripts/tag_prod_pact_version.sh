#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)

echo "Tagging with prod..."
pact-broker create-version-tag --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --tag prod --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_USERNAME -p $PACTBROKER_PASSWORD

echo "Verifying successfully tagged..."
DESCRIPTION=$(pact-broker describe-version --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_USERNAME -p $PACTBROKER_PASSWORD)
if [[ $DESCRIPTION == *"prod"* ]]
  then
    echo "Successfully tagged as prod"
  else
    echo "Failed as broker not successfully tagged"
    exit 1
fi
