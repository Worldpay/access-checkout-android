#!/bin/bash
set -e

HASH_CODE=$(git rev-parse --short HEAD)
VERSION="$PROJECT_VERSION+$HASH_CODE"
VT_FILE=${LIBRARY_MODULE}/build/pacts/access-checkout-android-sdk-verified-tokens.json
SESSIONS_FILE=${LIBRARY_MODULE}/build/pacts/access-checkout-android-sdk-sessions.json
VT_URL=https://${PACTBROKER_URL}/pacts/provider/verified-tokens/consumer/access-checkout-android-sdk/version/${VERSION}
SESSIONS_URL=https://${PACTBROKER_URL}/pacts/provider/sessions/consumer/access-checkout-android-sdk/version/${VERSION}

echo "Uploading pact file for verified tokens { file = ${VT_FILE} , version = ${VERSION} , url = ${VT_URL}}"
curl --fail --show-error -v -X PUT \
  -H "Content-Type: application/json" \
  -d@"${VT_FILE}" \
  -u "${PACTBROKER_USERNAME}:${PACTBROKER_PASSWORD}" \
  "${VT_URL}"

echo ""
echo ""

echo "Uploading pact file for sessions { file = ${SESSIONS_FILE} , version = ${VERSION} , url = ${SESSIONS_URL}}"
curl --fail --show-error -v -X PUT \
  -H "Content-Type: application/json" \
  -d@"${SESSIONS_FILE}" \
  -u "${PACTBROKER_USERNAME}:${PACTBROKER_PASSWORD}" \
  "${SESSIONS_URL}"

if [ "${BITRISE_GIT_BRANCH}" == "master" ]
  then
    echo "Tagging broker with master..."
    pact-broker create-version-tag --pacticipant access-checkout-android-sdk --version "${VERSION}" --tag "${BITRISE_GIT_BRANCH}" --broker-base-url "https://${PACTBROKER_URL}" -u "${PACTBROKER_USERNAME}" -p "${PACTBROKER_PASSWORD}"
fi


