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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.Objects;

import gr.streetthugssalonika.Interfaces.ApiSTS;
import gr.streetthugssalonika.Models.LocationModel;
import gr.streetthugssalonika.Models.PolyLineModel;
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

    private GoogleMap mMap;

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String BASE_URL = "http://localhost/";

    Boolean mLocationPermissionsGranted = false;

    FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng lastKnownLocation = null;

    //Hard Coded for DEV
    Integer userId = 1;

    Polyline polyline;

    Retrofit retrofit;
    ApiSTS apiInterfaces;

    private String polyLine = "";
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gr.streetthugssalonika.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getPolyLines();

    }

    private void getPolyLines() {

        apiInterfaces = retrofit.create(ApiSTS.class);

        Call<List<PolyLineModel>> call = apiInterfaces.getPolyLineById(1);

        call.enqueue(new Callback<List<PolyLineModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<PolyLineModel>> call, @NonNull Response<List<PolyLineModel>> response) {

                if (response.isSuccessful()) {
                    polyLine = Objects.requireNonNull(response.body()).get(0).getPolyline();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<PolyLineModel>> call, @NonNull Throwable t) {
                Toast.makeText(MapsActivity.this, "Failed to get Polyline", Toast.LENGTH_SHORT).show();

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

        // Add a marker in start and move the camera
        LatLng startLatLong = new LatLng(40.62618, 22.94857);

        mMap.addMarker(new MarkerOptions()
                .snippet("Starting at 20:00")
                .position(startLatLong)
                .title("Rally Start Point")
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.start)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLong, 12f));

        if ((!Objects.equals(polyLine, ""))) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .width(14)
                    .color(Color.BLUE)
                    .addAll(PolyUtil.decode(polyLine));

            polyline = mMap.addPolyline(polylineOptions);
        }

        LatLng endLatLong = new LatLng(40.57852, 22.97423);

        mMap.addMarker(new MarkerOptions()
                .position(endLatLong)
                .snippet("Ending at 22:00")
                .title("Rally end Point")
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.finish)));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        Log.d(TAG, "onLocationChanged: lat: " + location.getLatitude() + " long: " + location.getLongitude());
        sendLocation(userId, location);
    }

    private void sendLocation(Integer userId, @NonNull Location location) {

        apiInterfaces = retrofit.create(ApiSTS.class);

        Call<List<LocationModel>> call = apiInterfaces.sendCurrentLocationById(userId, Double.toString(location.getLongitude()), Double.toString(location.getLatitude()));

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
                Toast.makeText(MapsActivity.this, "Failed to send location", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "location didn't send");

            }
        });


    }
}
