#!/bin/sh

NEW_VERSION=$1
BRANCH_NAME="version-update-$NEW_VERSION"
AAR_FILE=./access-checkout/build/outputs/aar/access-checkout-android-$NEW_VERSION.aar
JAVADOC_FILE=./access-checkout/build/libs/access-checkout-android-$NEW_VERSION-javadoc.jar
SOURCES_FILE=./access-checkout/build/libs/access-checkout-android-$NEW_VERSION-sources.jar
CURRENT_DATE=$(date +"%Y-%m-%d")

git checkout master
git pull

git branch -d "$BRANCH_NAME" 2> /dev/null
git push origin --delete "$BRANCH_NAME" 2> /dev/null

git checkout -b "$BRANCH_NAME"

sed -i '' -e "s/version=.*/version=$NEW_VERSION/g" ./access-checkout/gradle.properties

sed -i '' -e "s/### Unreleased/### [v$NEW_VERSION](https:\/\/github.com\/Worldpay\/access-checkout-android\/releases\/tag\/v$NEW_VERSION) - $CURRENT_DATE/g" ./CHANGELOG.md

sed -i '' "6i\\
### Unreleased\\
#### Added\\
\\
#### Changed\\
\\
" CHANGELOG.md

echo "Verifying release artifacts"
./gradlew clean :access-checkout:assembleRelease javadocJar sourcesJar

if [ -f "$AAR_FILE" ]; then
    echo "$AAR_FILE exists."
else
    echo "$AAR_FILE does not exist."
    exit 1
fi

if [ -f "$JAVADOC_FILE" ]; then
    echo "$JAVADOC_FILE exists."
else
    echo "$JAVADOC_FILE does not exist."
    exit 1
fi

if [ -f "$SOURCES_FILE" ]; then
    echo "$SOURCES_FILE exists."
else
    echo "$SOURCES_FILE does not exist."
    exit 1
fi

echo "Pushing to git branch - origin/$BRANCH_NAME"
git add ./access-checkout/gradle.properties
git add ./CHANGELOG.md
git commit -m "Updated Android SDK version to $NEW_VERSION"
git push --set-upstream origin "$BRANCH_NAME"

echo "*******************************************************************"
echo "Version updated to $NEW_VERSION"
echo "Please ensure that the CHANGELOG.md has been updated accordingly"
echo "*******************************************************************"
