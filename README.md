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

For full integration instructions and code examples visit [Worldpay Developers](https://developer.worldpay.com/docs/access-worldpay/checkout/android)


