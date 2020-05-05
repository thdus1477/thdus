package com.example.geofence_prac;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeoFenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;

    public GeoFenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence){
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)      //싱글 지오펜스? 옵션 더있었어 확인해
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
        }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes){

        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude,latLng.longitude,radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)    //들어가서 얼마나 알람 있게 할거냐고?
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public PendingIntent getPendingIntent(){
        if(pendingIntent != null){
            return pendingIntent;
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,2607,intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public String getErrorString(Exception e){
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes
                    .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFNECE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFNECE_TOO_MANY_GEOFENCE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFNECE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
}
