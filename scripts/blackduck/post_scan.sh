#!/bin/bash

SDK_VERSION=$(grep 'version=' access-checkout/gradle.properties | cut -d= -f2)
REPORT_SDK_VERSION=$(grep 'version=' access-checkout/gradle.properties | cut -d= -f2 | sed -e 's/\./_/g')
if [ $CODEBUILD_BUILD_SUCCEEDING -eq 1 ]; then
  emoji=":duck:"
  color="#31AD72"
  header="Success"
  text="Blackduck Scan for Access Checkout Android build *No.${CODEBUILD_BUILD_NUMBER}* for *${CODEBUILD_SOURCE_VERSION}* for version *${SDK_VERSION}* has completed successfully."
  location="Report can be retrieved from *GW2 Dev* here: https://s3.console.aws.amazon.com/s3/object/wp-hydra-artefact-bucket?region=eu-west-1&prefix=access-checkout-android-blackduck/${CODEBUILD_BUILD_NUMBER}/Access_Checkout_Android_30000270_${REPORT_SDK_VERSION}_BlackDuck_RiskReport.pdf"
else
  emoji=":duck:"
  color="#EA5E1A"
  header="Failed"
  text="Blackduck Scan for Access Checkout Android build *No.${CODEBUILD_BUILD_NUMBER}* for *${CODEBUILD_SOURCE_VERSION}* for version *${SDK_VERSION}* has failed."
  location="No report generated"
fi

payload="{
	\"channel\":\"#hydra-blackduck\",
	\"username\":\"Access Checkout Android Blackduck Scan\",
	\"icon_emoji\": \"${emoji}\",
	\"attachments\": [
		{
			\"color\": \"${color}\",
			\"blocks\": [
				{
					\"type\": \"header\",
					\"text\": {
						\"type\": \"plain_text\",
						\"text\": \"${header}\"
					}
				},
				{
					\"type\": \"context\",
					\"elements\": [
						{
							\"type\": \"mrkdwn\",
							\"text\": \"${text}\"
						},
						{
							\"type\": \"mrkdwn\",
							\"text\": \"${location}\"
						},
					]
				}
			]
		}
	]
}"

result=$(curl -v -X POST --data-urlencode "payload=${payload}" ${SLACK_WEBHOOK_URL})

if [ "${result}" != "ok" ]; then
  echo $result
  exit 1
fi

