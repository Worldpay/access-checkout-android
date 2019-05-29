# Access Checkout Android SDK


A lightweight library and sample app that generates a Worldpay session reference from payment card data.
It includes, optionally, custom Android views that identifies card brands and validates payment cards and card expiry dates.


<img width="300" alt="app02" src=https://github.com/com-worldpay-gateway/checkout-android/blob/master/images/sample.png>

## Download

Download the latest AAR from [Maven Central](https://search.maven.org/search?q=worldpay) or grab via Gradle:

`implementation 'com.worldpay.access:access-checkout-android:XXXXXX`


or Maven:

```
<dependency>
  <groupId>com.worldpay.access</groupId>
  <artifactId>access-checkout-android</artifactId>
  <version>XXXXXX</version>
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
implementation (name:'access-checkout-android-release', ext:'aar')
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.3.31"
```


## Integration

### Basics


`AccessCheckoutClient` is the class you will need to initialise, at the very least, in order to use the Access Checkout SDK. It will be the class that you will interact with in order to generate a session state from
the Access Worldpay services. In order for you to do this, you must first call the `init()` method of this class and provide some dependencies, more details on this will be explained later.

`AccessCheckoutCard` is the coordinator class between the view inputs, the (optional) validations of those inputs, and the callback of those validation results to your `CardListener`

Our SDK is fully customizable and provides default card views that implement the required interfaces out of the box. For advanced styling you may choose to override certain aspects of it or implement the required interfaces yourself (interfaces are described at the end of the section)

Firstly, you will need to define the card views in a layout configuration file:
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


Having inflated the custom views, initialize the `AccessCheckoutClient`. You may wish to do this at any point in your `Application`'s lifecycle, but this will need to be done prior to the point at which the card details should be submitted.

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

When the form data is ready to be submitted, call the `generateSessionState` in order to create a session state, passing along the data from each of the fields:

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

In order to take advantage of using the in-built validation on the card fields, then you will need to do
some additional set up of the SDK. 

```
val card = AccessCheckoutCard(
        applicationContext, // Context
        panView,            // The PAN view
        cardCVVText,        // The CVV view
        cardExpiryText      // The Expiry view
  )
card.cardListener = this    // or wherever the reference to your `CardListener` is

panView.cardViewListener = card
cardCVVText.cardViewListener = card
cardExpiryText.cardViewListener = card
```

It is important that if you do wish to take advantage of our validation rules, that you implement the 
following interface for the callbacks: `CardListener`. These include all the functions which will be invoked
by the SDK when the fields have finished validating on different user events, for e.g. on each key stroke, on a field losing focus etc.

See an example of this interface being implemented in our sample application in the `MainActivity`
