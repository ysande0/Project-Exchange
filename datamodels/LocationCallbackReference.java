package com.syncadapters.czar.exchange.datamodels;

import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;

public class LocationCallbackReference extends LocationCallback {

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "MSG";
    private final SoftReference<LocationCallback> location_callback_reference;

    public LocationCallbackReference(LocationCallback location_callback){

        location_callback_reference = new SoftReference<>(location_callback);
    }

    @Override
    public void onLocationResult(@NotNull LocationResult location_result) {
        super.onLocationResult(location_result);



        // update()??

        if(location_callback_reference.get() != null) {
            Log.d(TAG, "[LocationCallbackReference] onLocationResult NOT NULL   : " + location_result.getLocations().size());
            location_callback_reference.get().onLocationResult(location_result);
        }

    }

    @Override
    public void onLocationAvailability(@NotNull LocationAvailability location_availability) {
        super.onLocationAvailability(location_availability);

        if(location_callback_reference.get() != null)
            location_callback_reference.get().onLocationAvailability(location_availability);

    }
}
