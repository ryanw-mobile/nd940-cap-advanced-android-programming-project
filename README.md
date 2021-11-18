## Political Preparedness [![Build Status](https://app.travis-ci.com/ryanwong-uk/nd940-cap-advanced-android-programming-project.svg?branch=main)](https://app.travis-ci.com/ryanwong-uk/nd940-cap-advanced-android-programming-project)

Political Preparedness is a Capstone project I have submitted for graduating from the Udacity
Android Developers Kotlin Nanodegree program in September 2021.

![Screenshot1](screenshots/screen0.png) ![Screenshot2](screenshots/screen1.png)
![Screenshot3](screenshots/screen2.png) ![Screenshot4](screenshots/screen3.png)
![Screenshot5](screenshots/screen4.png)

This app demonstrates the following views and techniques:

* [Retrofit](https://square.github.io/retrofit/) to make api calls to an HTTP web service.
* [Moshi](https://github.com/square/moshi) which handles the deserialization of the returned JSON to
  Kotlin data objects.
* [Glide](https://bumptech.github.io/glide/) to load and cache images by URL.
* [Room](https://developer.android.com/training/data-storage/room) for local database storage.
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) as a substitution of RxJava for
  asynchronous/non-blocking tasks.

It leverages the following components from the Jetpack library:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding adapters
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) with the
  SafeArgs plugin for parameter passing between fragments

In addition the following features are included:

* Device permission
* Device location

## Setting up the keystore

* Android keystore is not being stored in this repository. You need your own keystore to generate
  the apk / App Bundle

* You need to have your own Civic API key from Google:
  the [Google Developers Console](https://console.developers.google.com/)

* To ensure sensitive data are not being pushed to Git by accident, the keystore and its passwords
  are kept one level up of the project folder, so they are not managed by Git.

* If your project folder is at `/app/nd940-cap/`, the keystore file and `keystore.properties`
  should be placed at `/app/`

* The format of `keystore.properties` is:
  ```
     store=/app/release-key.keystore
     alias=<alias>
     pass=<alias password>
     storePass=<keystore password>
     civicApiKey="<your API Key here>"
  ```

## Building the App

### Build and install on the connected device

   ```
   ./gradlew installDebug
   // or
   // ./gradlew installRelease
   ```

* Options are: `Debug`, `Release`
* Debug builds will have an App package name suffix `.debug`

### Build and sign a bundle for distribution

After August 2021, all new apps and games will be required to publish with the Android App Bundle
format.

   ```
   ./gradlew clean bundleRelease
   ```

### Build and sign an apk for distribution

   ```
   ./gradlew clean assembleRelease
   ```

* The generated apk(s) will be stored under `app/build/outputs/apk/`
* Other usages can be listed using `./gradelew tasks`
