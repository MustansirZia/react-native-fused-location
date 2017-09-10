## Release Notes.

### 0.0.8
° Added `forceNewLocation` as an optional argument to `getFusedLocation`. This gets a new location everytime and never reuses a last known location.

° Added a check if GPS Provider or Network Provider exists on the device. Useful on emulators where `getFusedLocation` used to hang when GPS was turned off.

° Added `mocked` property to the `Location` object.

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
