package com.example.geofence_prac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    //MapsActivity mapsActivity;

    private static final String TAG = "GeoBroadcastReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofences triggered", Toast.LENGTH_SHORT).show();

        //String noti_title = mapsActivity.markerOptions.getTitle().toString();
        //String noti_body = mapsActivity.markerOptions.getSnippet().toString();

        //String noti_title = mapsActivity.address_editText.getText().toString();
        //String noti_body = mapsActivity.todo_editText.getText().toString();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event");
                return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        for(Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        //This location is the location at the point of trigger and NOT the center of the geofence
        //Location location = geofencingEvent.getTriggeringLocation();

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("설정한 장소에 들어옴","할 일을 확인하세요", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("설정한 장소에 위치하는 중", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("설정한 장소에서 나옴", "", MapsActivity.class);
                break;
        }
    }
}
