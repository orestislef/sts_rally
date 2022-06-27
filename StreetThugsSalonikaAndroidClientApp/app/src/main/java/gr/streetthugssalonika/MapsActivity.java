package gr.streetthugssalonika;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gr.streetthugssalonika.Interfaces.ApiSTS;
import gr.streetthugssalonika.Models.LocationModel;
import gr.streetthugssalonika.Models.PlacesModel;
import gr.streetthugssalonika.Models.PolyLineModel;
import gr.streetthugssalonika.Models.UserModel;
import gr.streetthugssalonika.databinding.ActivityMapsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        LocationListener {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String BASE_URL = "http://94.67.107.83:8080/";
    protected LocationManager locationManager;
    Boolean mLocationPermissionsGranted = false;

    FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng lastKnownLocation = null;

    //Hard Coded for DEV
    Integer userId = 1;

    Polyline polyline;

    Retrofit retrofit;
    ApiSTS apiInterfaces;

    List<UserModel> users;
    List<PolyLineModel> polylines;
    List<LocationModel> locations;
    List<PlacesModel> places;
    List<Marker> allLocationMarkers = new ArrayList<Marker>();

    private GoogleMap mMap;
    private String polyLine = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gr.streetthugssalonika.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterfaces = retrofit.create(ApiSTS.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);


    }

    private void removeAllMarkers() {
        for (Marker mLocationMarker: allLocationMarkers) {
            mLocationMarker.remove();
        }
        allLocationMarkers.clear();

    }


    private void getLocations() {
        locations = new ArrayList<>();
        Call<List<LocationModel>> call = apiInterfaces.getAllLocations();

        call.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationModel>> call, @NonNull Response<List<LocationModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        locations.addAll(response.body());
                        Log.d(TAG, "onResponse: lcoations: " + locations);

                        removeAllMarkers();

                        for (int i = 0; i < locations.size(); i++) {
                            LatLng latLng = new LatLng(Double.parseDouble(locations.get(i).getLatitude()), Double.parseDouble(locations.get(i).getLongitude()));

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(locations.get(i).getId());

                            Marker mLocationMarker = mMap.addMarker(markerOptions);
                            allLocationMarkers.add(mLocationMarker);

                        }

                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<LocationModel>> call, @NonNull Throwable t) {
                Toast.makeText(MapsActivity.this, "Failed to get Locations", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure:" + t.getMessage());
            }
        });
    }

    private void getPlaces() {
        places = new ArrayList<>();
        Call<List<PlacesModel>> call = apiInterfaces.getAllPlaces();

        call.enqueue(new Callback<List<PlacesModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<PlacesModel>> call, @NonNull Response<List<PlacesModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        places.addAll(response.body());
                        Log.d(TAG, "onResponse: places: " + places);

                        // Add a marker in start and move the camera
                        for (int i = 0; i < places.size(); i++) {
                            LatLng latLng = new LatLng(Double.parseDouble(places.get(i).getLatitude()), Double.parseDouble(places.get(i).getLongitude()));

                            if(Objects.equals(places.get(i).getName(), "start")){
                                mMap.addMarker(new MarkerOptions()
                                        .snippet("Starting at 20:00")
                                        .position(latLng)
                                        .title(places.get(i).getName())
                                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.start)));
                            }else if (Objects.equals(places.get(i).getName(), "end")){
                                mMap.addMarker(new MarkerOptions()
                                        .snippet("Ends at 21:00")
                                        .position(latLng)
                                        .title(places.get(i).getName())
                                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.finish)));
                            }


                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                        }


                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<PlacesModel>> call, @NonNull Throwable t) {
                Toast.makeText(MapsActivity.this, "Failed to get Places", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getUsers() {
        users = new ArrayList<>();
        Call<List<UserModel>> call = apiInterfaces.getAllUsers();

        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call, @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    users.addAll(response.body());
                    Log.d(TAG, "onResponse: users: " + users);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable
                    t) {
                Toast.makeText(MapsActivity.this, "Failed to get Polyline", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getPolyLines() {
        polylines = new ArrayList<>();
        Call<List<PolyLineModel>> call = apiInterfaces.getPolyLineById(1);

        call.enqueue(new Callback<List<PolyLineModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<PolyLineModel>> call, @NonNull Response<List<PolyLineModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        polylines.addAll(response.body());
                        Log.d(TAG, "onResponse: polyines: " + polylines);

                        PolylineOptions polylineOptions = new PolylineOptions()
                                .width(14)
                                .color(Color.BLUE)
                                .addAll(PolyUtil.decode(polylines.get(0).getPolyline()));

                        polyline = mMap.addPolyline(polylineOptions);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<PolyLineModel>> call, @NonNull Throwable t) {
                Toast.makeText(MapsActivity.this, "Failed to get Polyline", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

//        if(BuildConfig.APPLICATION_ID == "gr.orestis"){
//            userId = 0;
//        }
//        if(BuildConfig.APPLICATION_ID == "gr.vasilis" ){
//            userId = 1;
//        }

        //Runtime Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "onMapReady: permission OK");

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 30, this);

        }


        getLocations();
        getPolyLines();
        getUsers();
        getPlaces();

        Log.d(TAG, "onMapReady: polySize: " + polylines.size());
        if (polylines.size() > 0) {
            polyLine = polylines.get(0).getPolyline();
        }


        if ((!Objects.equals(polyLine, ""))) {

        }

        Log.d(TAG, "onMapReady: the Users size: " + users.size());
        //see how many users and add markers
        if (users.size() < 0 && locations.size() < 0) {
            Log.d(TAG, "onMapReady: Found " + users.size() + " users to report location");
            for (int i = 0; i < users.size(); i++) {
                Log.d(TAG, "onMapReady: User: " + users.get(i).getName());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(locations.get(i).getLatitude()), Double.parseDouble(locations.get(i).getLongitude())))//here to take from all location by id
                        .snippet("leader: " + users.get(i).getName())
                        .title("#" + (users.get(i).getId())) //to see if need for parse..here because of id(int) to string
                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_where_to_vote_24)));
            }
        } else {
            Log.d(TAG, "onMapReady: No Active users to follow");
        }
    }

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5 * 1000; //Delay for 15 seconds.  One second = 1000 milliseconds.


    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                //do something
                getLocations();
                Log.d(TAG, "run: you enter the get Location loop");

                handler.postDelayed(runnable, delay);
            }
        }, delay);
        super.onResume();
    }

    // we need this to convert ic to bitmap
    @NonNull
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        Objects.requireNonNull(vectorDrawable).setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                mLocationPermissionsGranted = true;
                //initialize our map
                onMapReady(mMap);
            }
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG, "onMyLocationButtonClick: ");
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "onLocationChanged: Current Location:\t lat: " + location.getLatitude() + " long: " + location.getLongitude());
        if (userId != -1) {
            sendLocation(userId, location);
        }
    }

    private void sendLocation(Integer userId, @NonNull Location location) {
        Call<List<LocationModel>> call = apiInterfaces.sendCurrentLocationById(userId, location.getLongitude(), (location.getLatitude()));

        call.enqueue(new Callback<List<LocationModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationModel>> call, @NonNull Response<List<LocationModel>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MapsActivity.this, "Location Sent", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "location success");
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<LocationModel>> call, @NonNull Throwable t) {
                Toast.makeText(MapsActivity.this, "Location sent", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: callExecuted: " + call.isExecuted() + "Error: " + t.getMessage());
            }
        });


    }
}
