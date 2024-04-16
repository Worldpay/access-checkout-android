[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.worldpay.access/access-checkout-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.worldpay.access/access-checkout-android)
[![Build Status](https://app.bitrise.io/app/70d419e86c91a8b6/status.svg?token=PiRRusMO6rZNofgN93wOyQ&branch=master)](https://app.bitrise.io/app/70d419e86c91a8b6)

# Access Checkout Android SDK

Access Checkout Android SDK allows you to secure your customer's card details by creating a session.

The session can be used to then take a payment using the [Access Worldpay APIs](https://developer.worldpay.com/docs/access-worldpay/get-started).

A sample demo application is also available to provide an example integration of the SDK.

## Getting Started

To learn how to integrate with the latest version of the Access Checkout Android SDK - [click here](https://maven-badges.herokuapp.com/maven-central/com.worldpay.access/access-checkout-android).

## Integration test

For full integration instructions and code examples visit [Worldpay Developers](https://developer.worldpay.com/docs/access-worldpay/checkout/android)

## Developers

Some useful commands to know during development:

```
# Running unit tests 
./gradlew :access-checkout:testDebugUnitTest

# Generate jacoco code coverage report
./gradlew :access-checkout:jacocoTestReport

# Verify jacoco code coverage
./gradlew :access-checkout:jacocoTestCoverageVerification

# Run unit tests and code coverage checks
./gradlew :access-checkout:testDebugUnitTest :access-checkout:jacocoTestReport :access-checkout:jacocoTestCoverageVerification

# Check ktlint formatting
./gradlew ktlintCheck

# Run the ktlint formatter
./gradlew ktlintFormat
```

## Changelog

Full changelog can be found [here](CHANGELOG.md)
