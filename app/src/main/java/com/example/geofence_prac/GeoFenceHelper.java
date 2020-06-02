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

import java.util.List;

public class GeoFenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours
    PendingIntent pendingIntent;

    public GeoFenceHelper(Context base) {
        super(base);
    }

    //지오펜싱 및 초기 트리거 지정
    //모니터링할 지오펜싱을 지정하고 관련 지오펜싱 이벤트가 트리거되는 방법 설정
    public GeofencingRequest getGeofencingRequest(List<Geofence> geofence){
        return new GeofencingRequest.Builder()
                //.addGeofence(geofence)
                .addGeofences(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
        }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes){

        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude,latLng.longitude,radius)  // 위치 및 반경(m)
                .setRequestId(ID)        // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)        //머무는 체크 시간
                .setExpirationDuration(GEOFENCE_TIMEOUT)   // Geofence 만료 시간
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

    //지오펜싱 에러메세지
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
