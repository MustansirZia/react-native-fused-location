## Release Notes.

### 0.5.0
° Google play services version can now be overriden and is defaulted to `16.+`.
° Android gradle version updated to `3.3.2`.
• Compile SDK version defaulted to `28`.
•  Build tools version defaulted to `28.0.3`.

### 0.4.0
° `BuildToolsVersion` and `sdkVersion` are now taken from the main android project's gradle file.

### 0.2.0
° `LocationCallback` added instead of `LocationListener` to method `getFusedLocation()` when last location returned null or forceNewLocation was set to true. This would guarantee the promise is resolved or rejected depending upon location availability.

### 0.1.0
° Made `areProvidersAvailable` method public under `FusedLocation.areProvidersAvailable()`.
° Semver for this repo started.

### 0.0.11
° Add `timestamp` via [`getTime()`](https://developer.android.com/reference/android/location/Location.html#getTime()) to the `location` object. Returns the UNIX timestamp (in millis) of when the location was generated.

### 0.0.8
° Added `forceNewLocation` as an optional argument to `getFusedLocation`. This gets a new location everytime and never reuses a last known location.

° Added a check if GPS Provider or Network Provider exists on the device. Useful on emulators where `getFusedLocation` used to hang when GPS was turned off.

° Added `mocked` property to the `Location` object.

• PR for #1 and #3 from - https://github.com/ginosi.

### 0.0.5
° Fixed typo in ReadMe. Changed `setLocationFatestInterval` to `setFastestLocationInterval` in js example.

### 0.0.4
° Fixed typo in ReadMe. Changed `FusedLocation` to `FusedLocationPackage` in manual linking.
PR by - https://github.com/jarvisluong

### 0.0.3
° Default values added to Readme.

### 0.0.2
° iOS compatibility fixes.

### 0.0.1
° Initial Commit.
