package com.mustansirzia.fused;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Written with ❤! By M on 10/06/17.
 */

public class FusedLocationModule extends ReactContextBaseJavaModule {

    private static final String TAG = "REACT_NATIVE_FUSED_LOCATION";
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 2404;
    private final String NATIVE_EVENT = "fusedLocation";
    private final String NATIVE_ERROR = "fusedLocationError";
    private int mLocationInterval = 15000;
    private int mLocationPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private int mLocationFastestInterval = 10000;
    private int mSmallestDisplacement = 0;
    private LocationListener mLocationListener;
    private GoogleApiClient mGoogleApiClient;

    public FusedLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "FusedLocation";
    }

    @ReactMethod
    public void setLocationInterval(int mLocationInterval) {
        this.mLocationInterval = mLocationInterval;
    }

    @ReactMethod
    public void setLocationPriority(int mLocationPriority) {
        switch (mLocationPriority) {
            case 0:
                this.mLocationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                break;
            case 1:
                this.mLocationPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
            case 2:
                this.mLocationPriority = LocationRequest.PRIORITY_LOW_POWER;
                break;
            case 3:
                this.mLocationPriority = LocationRequest.PRIORITY_NO_POWER;
                break;
        }
    }

    @ReactMethod
    public void setFastestLocationInterval(int mLocationFastestInterval) {
        this.mLocationFastestInterval = mLocationFastestInterval;
    }

    @ReactMethod
    public void setSmallestDisplacement(int mSmallestDisplacement) {
        this.mSmallestDisplacement = mSmallestDisplacement;
    }

    @ReactMethod
    public void getFusedLocation( boolean forceNewLocation, final Promise promise) {
        try {
            if (!areProvidersAvailable()) {
                promise.reject(TAG, "No location provider found.");
                return;
            }
            if (!checkForPlayServices()) {
                promise.reject(TAG, "Install Google Play Services First and Try Again.");
                return;
            }
            if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                promise.reject(TAG, "Appropriate permissions not given.");
                return;
            }
            final GoogleApiClient googleApiClient;
            LocationRequest request = buildLR();
            googleApiClient = new GoogleApiClient.Builder(getReactApplicationContext())
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.blockingConnect();
            final Location location;
            if(!forceNewLocation) {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
            else {
                location = null;
            }
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location l) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                        googleApiClient.disconnect();
                        promise.resolve(convertLocationToJSON(l));
                    }
                });
            } else {
                promise.resolve(convertLocationToJSON(location));
                googleApiClient.disconnect();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Native Location Module ERR - " + ex.toString());
            promise.reject(TAG, ex.toString());
        }
    }

    @ReactMethod
    public void startLocationUpdates() {
        try {
            if (!checkForPlayServices()) {
                WritableMap params = new WritableNativeMap();
                params.putString("error", "Play services not found.");
                sendEvent(getReactApplicationContext(), NATIVE_ERROR, params);
                return;
            }
            if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                WritableMap params = new WritableNativeMap();
                params.putString("error", "Appropriate permissions not given.");
                sendEvent(getReactApplicationContext(), NATIVE_ERROR, params);
                return;
            }
            if (!areProvidersAvailable()) {
                WritableMap params = new WritableNativeMap();
                params.putString("error", "No location provider found.");
                sendEvent(getReactApplicationContext(), NATIVE_ERROR, params);
                return;
            }
            LocationRequest request = buildLR();
            Log.e("request", request.getPriority() + "");
            mGoogleApiClient = new GoogleApiClient.Builder(getReactApplicationContext())
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.blockingConnect();
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location l) {
                    sendEvent(getReactApplicationContext(), NATIVE_EVENT, convertLocationToJSON(l));
                }
            };
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, mLocationListener);
        } catch (Exception ex) {
            Log.e(TAG, "Native Location Module ERR - " + ex.toString());
            WritableMap params = new WritableNativeMap();
            params.putString("error", "Native Location Module ERR - " + ex.toString());
            sendEvent(getReactApplicationContext(), NATIVE_ERROR, params);
        }
    }

    @ReactMethod
    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mLocationListener != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        }
    }

    // ~ https://stackoverflow.com/questions/
    // 22493465/check-if-correct-google-play-service-available-unfortunately-application-has-s
    private boolean checkForPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability
                .isGooglePlayServicesAvailable(getReactApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getCurrentActivity(), resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private WritableMap convertLocationToJSON(Location l) {
        WritableMap params = new WritableNativeMap();
        params.putDouble("latitude", l.getLatitude());
        params.putDouble("longitude", l.getLongitude());
        params.putDouble("accuracy", l.getAccuracy());
        params.putDouble("altitude", l.getAltitude());
        params.putDouble("bearing", l.getBearing());
        params.putString("provider", l.getProvider());
        params.putDouble("speed", l.getSpeed());
        params.putBoolean("mocked", l.isFromMockProvider());
        return params;
    }

    private LocationRequest buildLR() {
        LocationRequest request = new LocationRequest();
        request.setPriority(mLocationPriority);
        request.setInterval(mLocationInterval);
        request.setFastestInterval(mLocationFastestInterval);
        request.setSmallestDisplacement(mSmallestDisplacement);
        return request;
    }

    private boolean areProvidersAvailable() {
        LocationManager lm = (LocationManager)getReactApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return gps_enabled;
    }

    /*
  * Internal function for communicating with JS
  */
    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } else {
            Log.i(TAG, "Waiting for CatalystInstance...");
        }
    }
}
