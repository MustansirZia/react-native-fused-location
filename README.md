# react-native-fused-location

Get the finest location on Android using <a href="https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi"> Fused </a> API. <br /> <br />
I created this native module with an inspiration that none of react native's location libraries use the newer Fused API to get location. According to google, it is the most accurate way to get location in an Android device and judges by itself when to use GPS or cell towers/wifi. Thus, it works with both.
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

```diff
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
             bearing: 0,
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
## Release Notes.       
See <a href="https://github.com/MustansirZia/react-native-fused-location/blob/master/CHANGELOG.md"> CHANGELOG.md</a>.     

## License.
See <a href="https://github.com/MustansirZia/react-native-fused-location/blob/master/LICENSE.txt"> License</a>.