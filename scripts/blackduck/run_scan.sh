#!/bin/bash
echo "Setting version of Detect"
export DETECT_LATEST_RELEASE_VERSION=$blackduck_detect_version

echo "Initiating Blackduck Scan..."

curl -LOk https://detect.synopsys.com/detect.sh
chmod +x ./detect.sh
SDK_VERSION=$(grep 'version=' access-checkout/gradle.properties | cut -d= -f2)
./detect.sh --blackduck.url="https://fis2.app.blackduck.com/" --blackduck.api.token=$hydra_aco_blackduck_token --blackduck.trust.cert=true --detect.project.name=$BLACKDUCK_PROJECT_NAME --detect.project.version.name=$SDK_VERSION --detect.risk.report.pdf=true --detect.cleanup=false
