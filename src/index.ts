/**
 * Created by M on 11/06/17. With ❤
 */

import { NativeModules, DeviceEventEmitter, Platform } from 'react-native';

type EventName = 'fusedLocation' | 'fusedLocationError';

type LocationAccuracy = 0 | 1 | 2 | 3;

type LocationCallback = (location: Location) => void;

interface Location {
    latitude: number;
    longitude: number;
    speed: number;
    altitude: number;
    provider: string;
    accuracy: number;
    bearing: number;
    mocked: boolean;
    timestamp: string;
}

interface Subscription {
    listener: () => void;
    eventName: string;
}

const FusedLocation = NativeModules.FusedLocation;

const checkforAndroid = (): void => {
    if (Platform.OS !== 'android') {
        throw new Error('react-native-fused-location cannot be used any other platform other than android.');
    }
};

export default {
    getFusedLocation: (forceNewLocation: boolean): Promise<Location> => {
        checkforAndroid();
        return FusedLocation.getFusedLocation(forceNewLocation);
    },
    startLocationUpdates: (): Promise<void> => {
        checkforAndroid();
        return FusedLocation.startLocationUpdates();
    },
    stopLocationUpdates: (): Promise<void> => {
        checkforAndroid();
        return FusedLocation.stopLocationUpdates();
    },
    on: (eventName: EventName, cb: LocationCallback): Subscription => {
        checkforAndroid();
        if (eventName != 'fusedLocation' && eventName != 'fusedLocationError') {
            throw new Error("Event name has to be one of 'fusedLocation' or 'fusedLocationError'");
        }
        return { listener: DeviceEventEmitter.addListener(eventName, cb).listener, eventName };
    },
    off: (subscription: Subscription): void => {
        checkforAndroid();
        DeviceEventEmitter.removeListener(subscription.eventName, subscription.listener);
    },
    setLocationPriority: (priority: LocationAccuracy): Promise<void> => {
        checkforAndroid();
        if (priority < 0 || priority > 3) {
            throw new Error('Invalid priority set for fused api');
        }
        return FusedLocation.setLocationPriority(priority);
    },
    setLocationInterval: (intervalInMillis: number): Promise<void> => {
        checkforAndroid();
        return FusedLocation.setLocationInterval(intervalInMillis);
    },
    setFastestLocationInterval: (intervalInMillis: number): Promise<void> => {
        checkforAndroid();
        return FusedLocation.setFastestLocationInterval(intervalInMillis);
    },
    setSmallestDisplacement: (displacementInMeters: number): Promise<void> => {
        checkforAndroid();
        return FusedLocation.setSmallestDisplacement(displacementInMeters);
    },
    areProvidersAvailable: (): Promise<boolean> => {
        checkforAndroid();
        return FusedLocation.areProvidersAvailable();
    },
    Constants: {
        HIGH_ACCURACY: 0,
        BALANCED: 1,
        LOW_POWER: 2,
        NO_POWER: 3,
    },
};
