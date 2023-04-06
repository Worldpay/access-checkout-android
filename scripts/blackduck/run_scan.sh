#!/bin/bash
echo "Setting version of Detect"
export DETECT_LATEST_RELEASE_VERSION=$BLACKDUCK_DETECT_VERSION

echo "Initiating Blackduck Scan..."
curl -LOk https://detect.synopsys.com/detect8.sh
mv  detect8.sh ./detect.sh
chmod +x ./detect.sh

SDK_VERSION=$(grep 'version=' access-checkout/gradle.properties | cut -d= -f2)
GRADLE_CONFIGURATIONS_TO_SCAN="releaseCompileClasspath,releaseRuntimeClasspath"

if [ $IS_RELEASE_SCAN -eq 1 ]
then
  echo "Executing Release Scan with name [version]-RELEASE"
  SDK_VERSION="${SDK_VERSION}-RELEASE"
fi

./detect.sh --blackduck.url="https://fis2.app.blackduck.com/" --blackduck.api.token=$hydra_aco_blackduck_token --blackduck.trust.cert=true --detect.project.name=$BLACKDUCK_PROJECT_NAME --detect.project.version.name=$SDK_VERSION --detect.risk.report.pdf=true --detect.gradle.included.configurations="$GRADLE_CONFIGURATIONS_TO_SCAN"
