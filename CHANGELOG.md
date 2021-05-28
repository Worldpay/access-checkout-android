# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Unreleased
#### Added
- Ability to disable Pan formatting by calling `disablePanFormatting()` when building `CardValidationConfig`

#### Changed
- All Pans that are entered will be formatted by default. All card brands except for Amex will be formatted as `XXXX-XXXX-XXXX-XXXX`. Amex will be formatted as `XXXX-XXXXXX-XXXXX`.

### [v2.1.0](https://github.com/Worldpay/access-checkout-android/releases/tag/v2.1.0) - 2020-03-15
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
