# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Unreleased
#### Added

#### Changed

### [v4.0.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v4.0.0) - 2024-02-08
#### Removed
- Removed deprecated functionality
  - `AccessCheckoutClientBuilder`: removed `merchantId()` method
  - `CardDetails.Builder`: removed support for passing pan, expiryDate and cvc as String
  - `CardValidationConfig.Builder`: removed support for passing pan, expiryDate and cvc as EditText instances
  - `CvcValidationConfig.Builder`: removed support for passing cvc as an EditText instance

### [v3.0.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v3.0.0) - 2024-01-17
#### Changed
- card sessions created by the SDK are now compatible with the simplified Payments API (to be released soon) and remain compatible with the Verified Tokens API.
  - card sessions URL are now in the form `https://access.worldpay.com/sessions/...` instead of `https://access.worldpay.com/verifiedTokens/sessions/...` before
- A new UI component called `AccessCheckoutEditText` has been introduced. It is dedicated to capturing and encapsulating shoppers card details to minimize merchants exposure to PCI Data
  - at a high level, merchants now pass references of instances of this UI component to our SDK and in return the SDK will do all the heavy lifting for you, making sure that merchants do not have to manipulate card details directly
  - this components allows merchants apps to be assessed against the lowest PCI standard (SAQ-A)
- The following functionality has been deprecated
  - Support for using `EditText` is deprecated and will be removed in the next major version
  - Support for passing directly card details to create an instance of `CardDetails` has been deprecated and will be removed in the next major version
  - Support for using `merchantId()` in `AccessCheckoutClientBuilder` to pass a Checkout ID is deprecated and replaced by `checkoutId()`. Support for `merchantId()` will be removed in the next major version

### [v2.6.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.6.0) - 2023-02-22
#### Removed
- Drop functionality which sets hints/placeholders on EditText provided by clients. It is the responsibility of the client to set whichever hint/placeholder they want in the language of their preference. Major version of the SDK has not been changed despite this functionality being dropped, the reason is that the SDK should never have had that functionality in the first place so this is classified as a bug fix

#### Added
- SDK now enforces numeric inputType for each EditText specified by the client

#### Fixed
- Fix an issue where pressing and maintaining the backspace key does not delete the entirety of the card number field (only occurring when pan formatting is enabled)
- Fix an issue where the caret would not move when attempting to insert digits towards the end of a card number

#### Changed
- Upgrade SDK and app to use API Level 33
- Upgrade Kotlin to 1.6.21
- Upgrade Kotlin coroutines to 1.5.2
- Upgrade Gradle to 7.4 and upgrade gradle-wrapper jar
- Refactor build pipeline to minimise duplication of workflows 
- Change UI tests to also run against latest versions of Android
- Fix issue preventing Jacoco from correctly calculating code coverage

### [v2.5.2](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.5.2) - 2023-01-23
#### Changed
- Fix an issue where when inserting a number digits resulting in a pan longer than the max allowed the application crashes due to the caret position calculations.

### [v2.5.1](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.5.1) - 2022-04-27
#### Changed
- Fix an issue when using 'androix.lifecycle:lifecycle-runtime' >= 2.3.0 where the AccessCheckoutClientBuilder (actually ActivityLifecycleObserver) throws an exception when not built on main UI thread

### [v2.5.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.5.0) - 2022-04-04
#### Added
- Add support of a dispose functionality in order to fix a memory leak when using the SDK in React Native

### [v2.4.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.4.0) - 2021-11-22
#### Changed
- Replaced deprecated ASyncTasks by coroutines 

### [v2.3.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.3.0) - 2021-10-18
#### Added
- Now supporting Android API Level 30

#### Changed
- Allowing initialising validation after the onResume function

### [v2.2.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.2.0) - 2021-09-07
#### Added
- Ability to enable Pan formatting by calling `enablePanFormatting()` when building `CardValidationConfig`

#### Changed
- All Pans that are entered will be formatted by default. All card brands except for Amex will be formatted as `XXXX-XXXX-XXXX-XXXX`. Amex will be formatted as `XXXX-XXXXXX-XXXXX`.
- Sanitise `BaseUrl` configuration value to remove trailing forward slashes, so that `http://localhost/` becomes `http://localhost`

### [v2.1.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.1.0) - 2021-03-15
#### Added
- Ability to specify the card brands to support for validation, all other card brands will no longer be valid during card validation

#### Changed
- Outgoing http requests are now using a `HttpsUrlConnection` instead of `HttpUrlConnection`

### [v2.0.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.0.0) - 2020-07-30
#### Added
- Additional card types are now supported: Diners. Discover, JCB, Maestro
- Added ability to create session with CVC session as well as card sessions (or both)

#### Changed
- Restructured the packages within the SDK and major refactoring has taken place to improve performance
- Able to use own android components instead of having to use SDK provided ones
- Validation events are now only fired when the Pan, Expiry Date or Cvc become invalid or valid

#### Removed
- No longer have any SDK provided android components
- Support for custom validation rules

### [v1.2.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v1.2.0) - 2019-07-18
#### Added
- Card logos can now be fetched from external endpoint

#### Changed
- Remote card configuration loading has been simplified by using a `CardConfigurationFactory`

#### Removed
- Local card brand logo resources for visa, amex and mastercard

#### Fixed
- Fix for issue when screen is rotated and field validation state is lost

### [v1.1.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v1.1.0) - 2019-06-27
#### Added
- Card configuration can now be fetched from external endpoint via `CardConfigurationClientImpl`
- Added new methods `isValid`, and `applyLengthFilter` to `CardView` interface
- Added `applyCardLogo` to `PANLayout` class
- Added new `CardTextView` interface
- Added new `CardDateView`, which was renamed from `DateCardView`

#### Changed
- The `sessionReference` property has been renamed to `sessionState` in `SessionResponseListener`
- Simplified `CardListener` to remove redundant interface methods
- Change of names to logo resources to match the names in the externally hosted file
- Making `CardConfiguration` optional on the `CardValidator`
- Updated Javadoc for public classes and interfaces

#### Removed
- Local card configuration file has been removed
- `getInsertedText()` has been removed from `CardView` interface, it is now part of `CardTextView`
- `DateCardView` has been renamed to `CardDateView`

### [v1.0.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v1.0.0) - 2019-05-30
#### Added
- First Release of Access Checkout SDK for Android

#### Fixed
- Fix for issue where a HTTP 500 was throwing up a deserialization issue instead of returning an `AccessCheckoutError`
- Fix issue with fields allowing more than max length being inserted on copy and paste
- Fix for expiry field standalone validation checks
