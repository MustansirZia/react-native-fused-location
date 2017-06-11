# react-native-fused-location

[![npm version](https://badge.fury.io/js/react-native-fused-location.svg)](https://badge.fury.io/js/react-native-fused-location)

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
+ import com.mustansirzia.fused.FusedLocation;
    
    @Override
        protected List<ReactPackage> getPackages() {
          return Arrays.<ReactPackage>asList(
              ...
+             new FusedLocation(),
              ...
              new MainReactPackage()
          );
        }
        
```

## Permissions.
Add this to your `AndroidManifest.xml`:

```xml
    ...
    <uses-permission android:name="android.permission.ACCESS_COURSE_LOCATION"/>
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
| `getFusedLocation` | Nil | `Location` | Call this once to get `Location`. Returns a promise.
| `startLocationUpdates` | Nil | Nil | Call this to start receiving location updates. <br /> **<b>Note</b>: You still need to subscribe to `fusedLocation` event. <br /> So, you need to call this before you call `FusedLocation.on`.
| `stopLocationUpdates` | Nil | Nil | Stop receiving location updates. Call this to stop listening to device's location updates.
| `on` | `eventName, callback` | `Subscription` | Subscribe to an event. The callback with `Location` updates is eventName is `fusedLocation`. <br /> Call this after you call `startLocationUpdates`
| `off` | `Subscription` | Nil | Unsubscribe from the corresponding subscription.

### Configuration.
#### `setLocationPriority(priority)`: <br />
Set location accuracy. `priority` be of the following types. <br />
<b>`FusedLocation.Constants.HIGH_ACCURACY`</b> Most accurate. Least battery efficient. Uses GPS only. <br />
<b>`FusedLocation.Constants.BALANCED`</b> Mixed. Chooses an appropriate provider. <br />
<b>`FusedLocation.Constants.LOW_POWER`</b> Least accurate. Most battery efficient. Uses Wifi/Cell Towers only. <br />
<b>`FusedLocation.Constants.NO_POWER`</b> Uses location updates from other apps (if they occur). Don't request location from your app.

#### `setLocationInterval(interval)` <br />
Set an approximate interval (in milliseconds) between each location updates. Please note that this interval may not be strictly followed. Updates may come faster or slower than the interval argument.

#### `setFastestLocationInterval(interval)` <br />
Set the minimum possible interval between location updates (in milliseconds).

#### `setSmallestDisplacement(interval)` <br />
Set smallest amount of displacement to occur after which the location update will be received.

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
        bearing: Number
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
        FusedLocation.setLocationFastestInterval(15000); 
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
             bearing: 0
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

## Release Notes.       
See <a href="https://github.com/MustansirZia/react-native-fused-location/blob/master/CHANGELOG.md"> CHANGELOG.md</a>.     

## License.
See <a href="https://github.com/MustansirZia/react-native-fused-location/blob/master/LICENSE.txt"> License</a>.