#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)

PROJECT_VERSION="$PROJECT_VERSION"

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -d@$LIBRARY_MODULE/target/pacts/access-checkout-android-sdk-verified-tokens.json \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacts/provider/verified-tokens/consumer/access-checkout-android-sdk/version/$PROJECT_VERSION+$HASH_CODE

curl --fail --show-error -v -XPUT \-H "Content-Type: application/json" \
  -d@$LIBRARY_MODULE/target/pacts/access-checkout-android-sdk-sessions.json \
  -u $PACTBROKER_USERNAME:$PACTBROKER_PASSWORD \
  https://$PACTBROKER_URL/pacts/provider/sessions/consumer/access-checkout-android-sdk/version/$PROJECT_VERSION+$HASH_CODE


if [ $BITRISE_GIT_BRANCH == "master" ]
  then
    echo "Tagging broker with master..."
    pact-broker create-version-tag --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --tag $BITRISE_GIT_BRANCH --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_URL -p $PACTBROKER_PASSWORD

    echo "Verifying tag successful..."
    DESCRIPTION=$(pact-broker describe-version --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_URL -p $PACTBROKER_PASSWORD)
    if [[ $DESCRIPTION == *"master"* ]]
      then
        echo "Successfully tagged as master"
      else
        echo "Failed as broker not successfully tagged"
        exit 1
    fi
  else
    echo "Verifying not tagged as master..."
    if [[ $DESCRIPTION == *"master"* ]]
      then
        echo "Failed as broker incorrectly tagged with master"
        exit 1
      else
        echo "Did not tag as on branch $BITRISE_GIT_BRANCH"
    fi
fi


