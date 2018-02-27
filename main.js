/**
 * Created by M on 11/06/17. With ❤
 */

import {NativeModules, DeviceEventEmitter, Platform} from 'react-native';

const FusedLocation = NativeModules.FusedLocation;
const eventNames = ['fusedLocation', 'fusedLocationError'];

const noIOS = () => console.warn('Fused location cannot be used for iOS.');

const Dumb = {
    getFusedLocation: noIOS,
    startLocationUpdates: noIOS,
    stopLocationUpdates: noIOS,
    on: () => noIOS,
    off: () => noIOS,
    setLocationPriority: noIOS,
    setLocationInterval: noIOS,
    setFastestLocationInterval: noIOS,
    setSmallestDisplacement: noIOS,
    areProvidersAvailable: noIOS,
    checkLocationSettings: noIOS,
};

const Location = Platform.OS === 'ios' ? Dumb : {
    getFusedLocation: forceNewLocation => forceNewLocation ? FusedLocation.getFusedLocation(true) : FusedLocation.getFusedLocation(false),
    startLocationUpdates: FusedLocation.startLocationUpdates,
    stopLocationUpdates: FusedLocation.stopLocationUpdates,
    on: (eventName, cb) => {
        if (eventNames.indexOf(eventName) === -1) {
            throw new Error('Event name has to be one of \'fusedLocation\' or \'fusedLocationError\'');
        }
        return {listener: DeviceEventEmitter.addListener(eventName, cb).listener, eventName};
    },
    off: subscription => DeviceEventEmitter.removeListener(subscription.eventName, subscription.listener),
    setLocationPriority: priority => {
        if (priority < 0 || priority > 3) {
            throw new Error('Invalid priority set for fused api');
        }
        FusedLocation.setLocationPriority(priority);
    },
    setLocationInterval: FusedLocation.setLocationInterval,
    setFastestLocationInterval: FusedLocation.setFastestLocationInterval,
    setSmallestDisplacement: FusedLocation.setSmallestDisplacement,
    areProvidersAvailable: FusedLocation.areProvidersAvailable,
    checkLocationSettings: (invokeDialogIfResolutionIsRequired = true) => FusedLocation.checkLocationSettings(invokeDialogIfResolutionIsRequired),
    Constants: {
        HIGH_ACCURACY: 0,
        BALANCED: 1,
        LOW_POWER: 2,
        NO_POWER: 3,
        LOCATION_SETTINGS_ALREADY_OPTIMAL: "LOCATION_SETTINGS_ALREADY_OPTIMAL",
        LOCATION_SETTINGS_RESOLUTION_REQUIRED: "LOCATION_SETTINGS_RESOLUTION_REQUIRED",
        LOCATION_SETTINGS_CHANGE_UNAVAILABLE: "LOCATION_SETTINGS_CHANGE_UNAVAILABLE",
        LOCATION_SETTINGS_RESOLVED: "LOCATION_SETTINGS_RESOLVED",
        LOCATION_SETTINGS_CANCELED: "LOCATION_SETTINGS_CANCELED",
    },
};

export default Location;
