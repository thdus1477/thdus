package com.example.geofence_prac;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    //주소 검색 (지오코딩)
    //final TextView result_textView = (TextView) findViewById(R.id.result_textView); // 결과창
    //final EditText address_editText = (EditText)findViewById(R.id.address_editText);

    Button Address_Num_Button, AddressMap_Button, save_Button,
            Save_Button, RemoveMarker_Button;
    TextView result_textView,
            textView_t, textView_l, textView4;
    EditText address_editText, todo_editText,
            Location_editText, Todo_editText;

    final Geocoder geocoder = new Geocoder(this);

    final MarkerOptions markerOptions = new MarkerOptions();        //final 확인
    //----------------

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 1001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1002;

    private float GEOFENCE_RADIUS = 100;

    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        save_Button = findViewById(R.id.save_Button);
        Address_Num_Button = findViewById(R.id.Address_Num_Button);
        AddressMap_Button = findViewById(R.id.AddressMap_Button);
        result_textView = findViewById(R.id.result_textView);
        address_editText = findViewById(R.id.address_editText);

        todo_editText = findViewById(R.id.todo_editText);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);


        //지오코딩 버튼처리----------------------------------
        Address_Num_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> list = null;


                String str = address_editText.getText().toString();
                try {
                    list = geocoder.getFromLocationName(
                            str, // 지역 이름
                            10); // 읽을 개수
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0) {
                        result_textView.setText("해당되는 주소 정보는 없습니다");
                    } else {
                        result_textView.setText(list.get(0).getAddressLine(0).toString());
                        //          list.get(0).getLatitude();        // 위도
                        //          list.get(0).getLongitude();    // 경도
                    }
                }
            }
        });

        //주소를 지도에 표시
        AddressMap_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소입력 후 지도버튼 클릭시 해당 위도경도값의 지도화면으로 이동
                List<Address> list = null;

                String str = address_editText.getText().toString();

                try {
                    list = geocoder.getFromLocationName
                            (str, // 지역 이름
                                    10); // 읽을 개수
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0) {
                        result_textView.setText("해당되는 주소 정보는 없습니다");
                    } else {
                        // 해당되는 주소로 카메라이동
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();
                        LatLng searchLocation = new LatLng(lat, lon);

                        permission(searchLocation);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation,16));
                    }
                }
            }
        });
        //지오코딩 버튼처리----------------------------------

        save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddLocationActivity.class);

                String title = markerOptions.getTitle();
                String address = markerOptions.getSnippet();

                //String location_title = address_editText.getText().toString();
                //String todo_snippet = todo_editText.getText().toString();

                intent.putExtra("title", title);
                intent.putExtra( "address", address);

                startActivity(intent);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);
        //mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    //permission 확인
    private void enableUserLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }else{
            //Ask for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                //We need to show uses a dialog for display why the permission is needed and than ask for the permission

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_ACCESS_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //We have the permission
                mMap.setMyLocationEnabled(true);
            }else{
                //We do not have permission
                Toast.makeText(this,"위치 접근 권한을 확인해주세요"
                        ,Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //We have the permission
                Toast.makeText(this, "지도버튼을 누르시거나 원하는 장소를 길게 눌러주세요",Toast.LENGTH_SHORT).show();
            }else{
                //We do not have permission
                Toast.makeText(this,"백그라운드 위치 접근이 Geofence에 필요합니다"
                        ,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng){

        if(Build.VERSION.SDK_INT >= 29){
            //We need background permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                tryAddingGeofence(latLng);
            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }else{
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        }else{
            tryAddingGeofence(latLng);
        }
    }

    //지도버튼 클릭시 지도길게 클릭과 동일한 퍼미션
    public void permission(LatLng latLng){

        if(Build.VERSION.SDK_INT >= 29){
            //We need background permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED){
                tryAddingGeofence(latLng);
            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }else{
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        }else{
            tryAddingGeofence(latLng);
        }
    }

    private void tryAddingGeofence(LatLng latLng){
        mMap.clear(); //이거하면 여러개 안되고 하나 누르면 다른거 지워져
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeofence(latLng,GEOFENCE_RADIUS);
    }

    private void addGeofence(LatLng latLng, float radius){
        Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER);
        //Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,
        //                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geoFenceHelper.getErrorString(e);
                        Log.d(TAG,"onFailure: " + errorMessage);
                    }
                });
    }

    private void addMarker(final LatLng latLng){
        //String markerTitle = "클릭하여 할 일을 추가하세요";
        //String markerSnippet = "마커를 지울 수도 있습니다";

        String location_title = address_editText.getText().toString();
        String todo_snippet = todo_editText.getText().toString();

        markerOptions.position(latLng);
        markerOptions.title(location_title);
        markerOptions.snippet(todo_snippet);

        mMap.addMarker(markerOptions);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(final Marker marker) {
                //create BottomSheetDialog

                /*final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                bottomSheetDialog.show();
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                RemoveMarker_Button = bottomSheetDialog.findViewById(R.id.RemoveMarker_Button);
                Save_Button = bottomSheetDialog.findViewById(R.id.Save_Button);
                textView_t = bottomSheetDialog.findViewById(R.id.textView_t);
                Todo_editText = bottomSheetDialog.findViewById(R.id.Todo_editText);
                textView_l = bottomSheetDialog.findViewById(R.id.textView_l);
                Todo_editText = bottomSheetDialog.findViewById(R.id.Location_editText);

                marker.setTitle("change title");

                RemoveMarker_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMap.clear();   //지도 전체가 다 지워져
                        //marker.remove();  //마커만 지워지고 반경은 남아
                        //bottomSheetDialog.dismiss();  // X

                    }
                });

                Save_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MarkerOptions markerOptions = new MarkerOptions();

                        String location_written = Location_editText.getText().toString();
                        String todo_written = Todo_editText.getText().toString();

                        markerOptions.title(location_written);
                        markerOptions.snippet(todo_written);

                        //X - 변수사용 확인필요
                    }
                });
                 */
            }
        });
    }

    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
}
