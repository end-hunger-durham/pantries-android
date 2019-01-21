# Durham Food Resources

An android application used to display food pantries in the Durham area and provide information and directions.

## Installation

Currently, the only way to install the application is by compiling the source code.  Here are the steps for installation:

1. Download [Android Studio](https://developer.android.com/studio/)
1. Open the `pantries-android` folder in Android Studio
1. Add the following line to `$HOME/.gradle/gradle.properties`: `pantries_android_maps_key="[GOOGLE MAPS API KEY]"`
    - Code for Durham developers can reach out to the owners of this repo for the EndHungerDurham Maps API key
    - Other developers can acquire their own key using [the following guide](https://developers.google.com/maps/documentation/android-sdk/signup)
1. Click the Run button (green play button) and test on [a connected Android device](https://developer.android.com/studio/run/device)
 or on [an Android emulator](https://developer.android.com/studio/run/emulator)
   - It is recommended to use an Android device if available
