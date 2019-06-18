# Access Checkout Android SDK


A lightweight library and sample app that generates a Worldpay session reference from payment card data.
It includes, optionally, custom Android views that identifies card brands and validates payment cards and card expiry dates.


<img width="300" alt="app02" src=https://github.com/com-worldpay-gateway/checkout-android/blob/master/images/sample.png>


## Download

Download the latest AAR from [Maven Central](https://search.maven.org/search?q=g:com.worldpay.access%20AND%20a:access-checkout-android) or include in your project's build dependencies via Gradle:

`implementation 'com.worldpay.access:access-checkout-android:1.0.0`


or Maven:

```
<dependency>
  <groupId>com.worldpay.access</groupId>
  <artifactId>access-checkout-android</artifactId>
  <version>1.0.0</version>
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
implementation (name:'access-checkout-android-1.0.0', ext:'aar')
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.31"
```


## Integration

### Basics


The main step required in order to use the Access Checkout SDK is the initialisation of an instance of `AccessCheckoutClient`. 
Interaction with this class will allow developers to obtain a session state from Access Worldpay services. 

This can be achieved by invoking the `init()` method of this class and by providing the required dependencies, more details provided in the following sections.

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

accessCheckoutClient = AccessCheckoutClient.init(
            getBaseUrl(),           // Base API URL 
            getMerchantID(),        // Your merchant ID
            sessionResponseListener,// SessionResponseListener
            applicationContext,     // Context
            lifecycleOwner          // LifecycleOwner
        )
```

When the form data is ready to be submitted, `generateSessionState` may be called in order to create a session state, passing along the data from each of the fields:

```
val pan = panView.getInsertedText()
val month = cardExpiryText.getMonth()
val year = cardExpiryText.getYear()
val cvv = cardCVVText.getInsertedText()
accessCheckoutClient.generateSessionState(pan, month, year, cvv)
```

#### Initialisation parameters
- Base API URL: Base URL for Access Worldpay services
- Merchant ID: Your registered Access Worldpay Merchant ID
- SessionResponseListener:  Reference to the listener which will receive the session response (or any service/API errors)
- Context:                  [Android Context](https://developer.android.com/reference/android/content/Context)
- LifecycleOwner:           [Android LifecycleOwner](https://developer.android.com/reference/android/arch/lifecycle/LifecycleOwner)


### Validation

In order to take advantage of the in-built validation on the card fields, there is an additional setup step. 

```
val card = AccessCheckoutCard(
        applicationContext, // Context
        panView,            // The PAN view
        cardCVVText,        // The CVV view
        cardExpiryText      // The Expiry view
  )
card.cardListener = this    // reference to `CardListener` implementation

panView.cardViewListener = card
cardCVVText.cardViewListener = card
cardExpiryText.cardViewListener = card
```

If taking advantage of the provided validators it is important to implement the `CardListener` interface for its callback methods.

These include all the functions invoked by the SDK at the time when the fields have finished validating on different user input events, for e.g. on each key stroke, on a field losing focus etc.

See an example of this interface being implemented in our sample application in the `MainActivity`


#### Receiving the Session State 


When the request for a session state starts, the `SessionResponseListener` is notified via the  `onRequestStarted()` callback method. 

When a result becomes available, the implementing class of `SessionResponseListener` will receive the callback holding the session state or an exception describing the error.

`onRequestFinished(sessionState: String?, error: AccessCheckoutException?)`

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
