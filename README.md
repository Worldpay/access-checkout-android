# Access Checkout Android SDK


A lightweight library and sample app that generates a Worldpay session reference from payment card data.
It includes, optionally, custom Android views that identifies card brands and validates payment cards and card expiry dates.


<img width="300" alt="app02" src=https://github.com/Worldpay/access-checkout-android/blob/master/images/sample.png>


## Download

Download the latest AAR from [Maven Central](https://search.maven.org/search?q=g:com.worldpay.access%20AND%20a:access-checkout-android) or include in your project's build dependencies via Gradle:

`implementation 'com.worldpay.access:access-checkout-android:1.2.0`


or Maven:

```
<dependency>
  <groupId>com.worldpay.access</groupId>
  <artifactId>access-checkout-android</artifactId>
  <version>1.2.0</version>
</dependency>
```

or Local AAR library integration:

Copy the library file into your `app/libs` folder

Add the `flatDir` repo to the project level `build.gradle` file:
```
allprojects {
    repositories {
        //...
        flatDir {
            dirs 'libs'
        }
    }
}
```

Add the following lines to the app level dependency list (kotlin version can be changed):

``` 
implementation (name:'access-checkout-android-1.2.0', ext:'aar')
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.31"
```


## Integration

### Basics


The main step required in order to use the Access Checkout SDK is the initialisation of an instance of `AccessCheckoutClient`. 
Interaction with this class will allow developers to obtain a session state from Access Worldpay services. 

This can be achieved by making use of the builder method for this class and by providing the required dependencies, more details provided in the following sections.

`AccessCheckoutCard` is the coordinator class between the view inputs, the (optional) validators of those inputs, and the callback of those validation results to an implementation of `CardListener`

The SDK is fully customizable and provides default card views that implement the required interfaces out of the box. For advanced styling, it is possible to override some of the default implementations or to provide fully customized implementations of the required interfaces. The interfaces are described at the end of the section.

Firstly, a layout configuration file with the card views is defined as below:
```
<com.worldpay.access.checkout.views.PANLayout
            android:id="@+id/panView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            .../>
<com.worldpay.access.checkout.views.CardExpiryTextLayout
            android:id="@+id/cardExpiryText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            .../>
<com.worldpay.access.checkout.views.CardCVVText
            android:id="@+id/cardCVVText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:hint="CVV"
            .../>
```


Having inflated the custom views, initialize the `AccessCheckoutClient`. Initialisation can be performed at any point suitable for the application, but it will need to be done prior to card details submission.

```
import com.worldpay.access.checkout.AccessCheckoutClient

var accessCheckoutClient: AccessCheckoutClient

val panView = findViewById<PANLayout>(R.id.panView)
val cardCVVText = findViewById<CardCVVText>(R.id.cardCVVText)
val cardExpiryText = findViewById<CardExpiryTextLayout>(R.id.cardExpiryText)

accessCheckoutClient = AccessCheckoutClientBuilder
                                .baseUrl(getBaseUrl())                              // Base API URL
                                .merchantId(getMerchantID())                        // Your merchant ID
                                .context(applicationContext)                        // Context
                                .sessionResponseListener(sessionResponseListener)   // SessionResponseListener
                                .lifecycleOwner(lifecycleOwner)                     // LifecycleOwner
                                .build()
```

When the form data is ready to be submitted, `generateSessionState` may be called in order to create a session state, passing along the data from each of the fields as CardDetails object and a list of requested SessionType (More detail later on this page)

```
val pan = panView.getInsertedText()
val month = cardExpiryText.getMonth()
val year = cardExpiryText.getYear()
val cvv = cardCVVText.getInsertedText()

val cardDetails = CardDetails.Builder()
                    .pan(pan)
                    .expiryDate(month, year)
                    .cvc(cvc)
                    .build()

generateSessionState(cardDetails, listOf(VERIFIED_TOKEN_SESSION))
```

#### Initialisation parameters
- Base API URL: Base URL for Access Worldpay services
- Merchant ID: Your registered Access Worldpay Merchant ID
- SessionResponseListener:  Reference to the listener which will receive the session response (or any service/API errors)
- Context:                  [Android Context](https://developer.android.com/reference/android/content/Context)
- LifecycleOwner:           [Android LifecycleOwner](https://developer.android.com/reference/android/arch/lifecycle/LifecycleOwner)


### Validation

In order to take advantage of the in-built validation on the card fields, there are additional setup steps.

#### 1) Instantiate the Card

Firstly, instantiate an instance of `AccessCheckoutCard`, passing in the references to the views:
```
val card = AccessCheckoutCard(
        panView,            // The PAN view
        cardCVVText,        // The CVV view
        cardExpiryText      // The Expiry view
  )
```

#### 2) Implement a `CardListener`

The `CardListener` includes the callback functions invoked by the SDK at the point when the fields have finished validating on different user input events, for e.g. on each key stroke, on a field losing focus etc.

```
override fun onUpdate(cardView: CardView, valid: Boolean) {
    cardView.isValid(valid)
    submit.isEnabled = card.isValid() && !loading
}

override fun onUpdateLengthFilter(cardView: CardView, inputFilter: InputFilter) {
    cardView.applyLengthFilter(inputFilter)
}

override fun onUpdateCardBrand(cardBrand: CardBrand?) {
    // CardBrand contains a reference to a list of images which you can
    // use to fetch the remotely hosted image for the identified card brand.
    // See an example of how to do this in MainActivity.kt in the sample app
}
```

`onUpdate` is the method that will be invoked when a field has finished validation. The `CardView` to apply the effect of the validation result to is returned,
alongside a property `valid` indicating whether that field is in a valid or invalid state.

`onUpdateLengthFilter` is the method which will be invoked when the card view needs to be updated with a reference to a length filter, for restricting
input length for that card view.

`onUpdateCardBrand` is the method which will be invoked when there has been an update to the identity of the card. A non-null reference to the particular `CardBrand`
will be returned if it an identified card brand, otherwise null if we have been unable to identify the card.

The card brand will contain a list of images which you may then use, if you wish, to display an icon for the identified card brand. Access Worldpay hosts both a PNG and an SVG
version of the supported card brands, which you can use right away and apply to your views. The sample application demonstrates usage of this and an example of how
to render an SVG image to our custom `PANLayout` class.

#### 3) Set the references

Once the required callback interface has been implemented, you now need to set the reference to it on the `AccessCheckoutCard` object.

```
card.cardListener = this    // reference to `CardListener` implementation
card.cardValidator = AccessCheckoutCardValidator()  // reference to an AccessCheckoutCardValidator implementation

panView.cardViewListener = card
cardCVVText.cardViewListener = card
cardExpiryText.cardViewListener = card

```

You will also need to set the reference to the `CardViewListener` on each of the views, which will be the instance of `AccessCheckoutCard` you created earlier. This is what binds the interactions on the views to the business logic inside the SDK.

If you wish to use our implementation of a `CardValidator` then you can do so by specifying an `AccessCheckoutCardValidator` instance as the `cardValidator` on the `AccessCheckoutCard`, or provide your own implementation by implementing the `CardValidator` interface
and plug that in instead.

#### 4) Fetch the remote card configuration

The validation logic, especially the PAN and the CVV fields, inside `AccessCheckoutCard` is based off of a `CardConfiguration`. Access Worldpay hosts a JSON based version of the card configuration file which 
is available to consume via a `CardConfigurationFactory`, and all that is required to set that up is as follows:

```
CardConfigurationFactory.getRemoteCardConfiguration(card, getBaseUrl())
```
Of course, you can build and provide your own `CardConfiguration` if you'd prefer:
```
val cardConfiguration = CardConfiguration(brands = ..., defaults = ...)
`card.cardValidator = AccessCheckoutCardValidator(cardConfiguration)`
```

#### Requesting the Session State/s

To receive a session state, as shown below, you must call the AccessCheckoutClient.generateSessionState method and pass the CardDetails object and a list the type of session state/s (SessionType) you would like to receive back.

```
val cardDetails = CardDetails.Builder()
                    .pan(pan)
                    .expiryDate(month, year)
                    .cvc(cvc)
                    .build()

generateSessionState(cardDetails, listOf(VERIFIED_TOKEN_SESSION))
```

##### Verified Token Session (VERIFIED_TOKEN_SESSION)

This is the session required for a standard card payment using a new card that is not saved on file.

##### Payments CVC Session (PAYMENTS_CVC_SESSION)

 At present this is only a requirement for mastercard payments if you are a gambling merchant. This session is required when making a repeat payment using a card with stored details but the cvc is required to be captured again for additional verification.

##### Receive both session types

Mastercard scheme rules require that gambling merchants need both a Verified Token Session and a Payment CVC Session for first time payments. (Subsequent payments will only require a Payments CVC Session)

#### Receiving the Session State

When the request for a session state starts, the `SessionResponseListener` is notified via the  `onRequestStarted()` callback method.

When a result becomes available, the implementing class of `SessionResponseListener` will receive the callback holding the session state or an exception describing the error.

`onRequestFinished(sessionState: List<String>?, error: AccessCheckoutException?)`

#### When things go wrong: `AccessCheckoutException`

If there is a problem, `SessionResponseListener` will be notified through the same `onRequestFinished(sessionState: String?, error: AccessCheckoutException?)` callback, this time with a `null` sessionState and non-null error.

The following table of errors can be found in the enum class `com.worldpay.access.checkout.api.AccessCheckoutException.Error`

| HTTP Code | Error name | Message |
| --- | --- | --- |
| 400 | bodyIsNotJson | The body within the request is not valid json |
| 400 | bodyIsEmpty | The body within the request is empty |
| 400 | bodyDoesNotMatchSchema | The json body provided does not match the expected schema |
| 404 | resourceNotFound | Requested resource was not found |
| 404 | endpointNotFound | Requested endpoint was not found |
| 405 | methodNotAllowed | Requested method is not allowed |
| 406 | unsupportedAcceptHeader | Accept header is not supported |
| 415 | unsupportedContentType | Content-type header is not supported |
| 500 | internalErrorOccurred | Internal server error |
| 500 | unknownError | Unknown error |


If presented with a `bodyDoesNotMatchSchema` error, a list of the broken validation rules may be provided to help with debugging the problem.

`AccessCheckoutClientError` is the subclass used for the above issues.
```
data class AccessCheckoutClientError(
        val error: Error,
        override val message: String?,
        val validationRules: List<ValidationRule>? = null
    ) : AccessCheckoutException()
```

The property `validationRules` contains a list of `ValidationRule`s which includes the broken rule, a description message and the JSON path to the offending property.

`data class ValidationRule(val errorName: ValidationRuleName, val message: String, val jsonPath: String)`
