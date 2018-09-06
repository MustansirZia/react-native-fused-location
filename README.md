# react-native-fused-location

[![npm version](https://badge.fury.io/js/react-native-fused-location.svg)](https://badge.fury.io/js/react-native-fused-location)
[![npm](https://img.shields.io/npm/dt/react-native-fused-location.svg)](https://www.npmjs.com/package/react-native-fused-location)
[![Package Quality](http://npm.packagequality.com/shield/react-native-fused-location.svg)](http://packagequality.com/#?package=react-native-fused-location)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)


Get the finest location on Android using <a href="https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi"> Fused </a> API. <br /> <br />
I created this react native module with an inspiration that none of react native's location libraries use the newer Fused API to get location. According to google, it is the most accurate way to get location in an Android device and judges by itself when to use GPS or cell towers/wifi. Thus, it works with both.
<br />

## Install
`npm install react-native-fused-location --save`
<br />
or
<br />
`yarn add react-native-fused-location`
<br />
#### Automatic Link.
`react-native link react-native-fused-location`
#### Manual Link.
• in `android/app/build.gradle:`

```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-fused-location')
}
```

• in `android/settings.gradle`:

```diff
...
include ':app'
+ include ':react-native-fused-location'
+ project(':react-native-fused-location').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-fused-location/android')
```
• in `MainApplication.java:`
```diff
+ import com.mustansirzia.fused.FusedLocationPackage;

    @Override
        protected List<ReactPackage> getPackages() {
          return Arrays.<ReactPackage>asList(
              ...
+             new FusedLocationPackage(),
              ...
              new MainReactPackage()
          );
        }

```

## Permissions.
Add this to your `AndroidManifest.xml`:

```xml
    ...
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    ...
        <permission
            android:name="android.permission.ACCESS_COARSE_LOCATION"
            android:protectionLevel="signature" />
        <permission
                android:name="android.permission.ACCESS_FINE_LOCATION"
                android:protectionLevel="signature"/>
    ...            
```

## Usage.

### API.
| Function | Arguments | Returns | Note |
|:---|:---:|:---:|:------|
| `getFusedLocation` | `forceNewLocation` | `Promise[Location]` | Call this once to get `Location`. Pass optional boolean `forceNewLocation` to get new location update. Otherwise return the last known location. Returns a promise.
| `startLocationUpdates` | Nil | `Promise[Nil]` | Call this to start receiving location updates. The function returns a promise that will resolve after the bootstrap of the Fused provider is done. <br /> **<b>Note</b>: You still need to subscribe to `fusedLocation` event. <br /> So, you need to call this before you call `FusedLocation.on`.
| `stopLocationUpdates` | Nil | `Promise[Boolean]` | Stop receiving location updates. Call this to stop listening to device's location updates. The function returns a promise that will resolve to a boolean reflecting if the updates were indeed stoped or not (if they were already stopped beforehand).
| `on` | `eventName, callback` | `Subscription` | Subscribe to an event. The callback is called with `Location` updates if the eventName is `fusedLocation`. <br /> Call this <b>after</b> you call `startLocationUpdates`
| `off` | `Subscription` | Nil | Unsubscribe from the corresponding subscription.
| `areProvidersAvailable` | Nil | `Promise[Boolean]` | Returns a promise that will always resolve to a boolean value. The resolved value reflects the providers' availability; true when location providers are available and false otherwise.

### Configuration.
#### `setLocationPriority(priority)` <br />
Set location accuracy. `priority` be of the following types. <br />
<b>`FusedLocation.Constants.HIGH_ACCURACY`</b> Most accurate. Least battery efficient. Uses GPS only. <br />
<b>`FusedLocation.Constants.BALANCED`</b> Mixed. Chooses an appropriate provider. <br />
<b>`FusedLocation.Constants.LOW_POWER`</b> Least accurate. Most battery efficient. Uses Wifi/Cell Towers only. <br />
<b>`FusedLocation.Constants.NO_POWER`</b> Uses location updates from other apps (if they occur). Don't request location from your app. <br />
• Default `FusedLocation.Constants.BALANCED`<br />

#### `setLocationInterval(interval)` <br />
Set an approximate interval (in milliseconds) between each location updates. Please note that this interval may not be strictly followed. Updates may come faster or slower than the interval argument. <br />
• Default `15000`

#### `setFastestLocationInterval(interval)` <br />
Set the minimum possible interval between location updates (in milliseconds). <br />
• Default `10000`

#### `setSmallestDisplacement(displacement)` <br />
Set smallest amount of displacement (in meters) to occur after which the location update will be received. <br />
• Default `0`

For more info, see <a href="https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest"> here. </a>

### Types.
```
type Location {
        latitude: Number,
        longitude: Number,
        speed: Number,
        altitude: Number,
        heading: Number,
        provider: String,
        accuracy: Number,
        bearing: Number,
        mocked: Boolean,
        timestamp: String
}
```
```
type Subscription {
        listener: Function,
        eventName: String
}
```

### Example.
```js
...
import FusedLocation from 'react-native-fused-location';
...

async componentDidMount() {
     const granted = await PermissionsAndroid.request(
                    PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION, {
                        title: 'App needs to access your location',
                        message: 'App needs access to your location ' +
                        'so we can let our app be even more awesome.'
                        }
                    );
     if (granted) {

        FusedLocation.setLocationPriority(FusedLocation.Constants.HIGH_ACCURACY);

        // Get location once.
        const location = await FusedLocation.getFusedLocation();
        this.setState({lat: location.latitude, long: location.longitude});

        // Set options.
        FusedLocation.setLocationPriority(FusedLocation.Constants.BALANCED);
        FusedLocation.setLocationInterval(20000);
        FusedLocation.setFastestLocationInterval(15000);
        FusedLocation.setSmallestDisplacement(10);


        // Keep getting updated location.
        FusedLocation.startLocationUpdates();

        // Place listeners.
        this.subscription = FusedLocation.on('fusedLocation', location => {
           /* location = {
             latitude: 14.2323,
             longitude: -2.2323,
             speed: 0,
             altitude: 0,
             heading: 10,
             provider: 'fused',
             accuracy: 30,
             bearing: 0,
             mocked: false,
             timestamp: '1513190221416'
           }
           */
           console.log(location);
        });

        /* Optional
        this.errSubscription = FusedLocation.on('fusedLocationError', error => {
            console.warn(error);
        });
        */
     }

...

componentWillUnmount() {

    FusedLocation.off(this.subscription);
    // FusedLocation.off(this.errSubscription);
    FusedLocation.stopLocationUpdates();

}  

...

```
<br />

## Compatibility.
Tested with RN versions `> 0.40.x`. For other versions I haven't had the time to test. Feel free to.

Tested with Android SDK version `>= 16 (Android 4.1 - Jelly Bean)`. Please feel free to test it with other versions.

This repository follows [Semantic Versioning](https://semver.org/). No breaking changes will be incorporated till `v1.x.x`.

## Release Notes.       
See <a href="https://github.com/MustansirZia/react-native-fused-location/blob/master/CHANGELOG.md"> CHANGELOG.md</a>.     

## License.
See <a href="https://github.com/MustansirZia/react-native-fused-location/blob/master/LICENSE.txt"> License</a>.

[![NPM](https://nodei.co/npm/react-native-fused-location.png?downloads=true&downloadRank=true&stars=true)](https://nodei.co/npm/react-native-fused-location/)
