#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)
DESCRIPTION=$(pact-broker describe-version --pacticipant access-checkout-android-sdk --version $PROJECT_VERSION+$HASH_CODE --broker-base-url https://$PACTBROKER_URL -u $PACTBROKER_USERNAME -p $PACTBROKER_PASSWORD)

if [ $BITRISE_GIT_BRANCH == "master" ]
  then
    echo "Verifying tagged as master..."
    if [[ $DESCRIPTION == *"master"* ]]
      then
        echo "Successfully tagged as master"
      else
        echo "Failed as broker not successfully tagged"
        echo $DESCRIPTION
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
