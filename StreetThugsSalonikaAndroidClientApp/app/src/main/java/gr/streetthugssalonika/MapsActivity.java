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

import gr.streetthugssalonika.Interfaces.ApiInterfaces;
import gr.streetthugssalonika.Models.PolyLine;
import gr.streetthugssalonika.databinding.ActivityMapsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String BASE_URL = "http://localhost/";

    private Boolean mLocationPermissionsGranted = false;

    private FusedLocationProviderClient mfusedLocationProviderclient;
    private LatLng lastKnownLocation = null;

    Retrofit retrofit;
    ApiInterfaces apiInterfaces;

    private String polyLine = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
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

        apiInterfaces = retrofit.create(ApiInterfaces.class);

        Call<List<PolyLine>> call = apiInterfaces.getPolyLines(1);

        call.enqueue(new Callback<List<PolyLine>>() {
            @Override
            public void onResponse(Call<List<PolyLine>> call, Response<List<PolyLine>> response) {

                if(!response.isSuccessful()){
                    return;
                }else{
                   polyLine = response.body().get(0).getPolyline();
                }

            }

            @Override
            public void onFailure(Call<List<PolyLine>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Failedddddd", Toast.LENGTH_SHORT).show();

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "onMapReady: permission OK");
        }
        // Add a marker in start and move the camera
        LatLng startLatLong = new LatLng(40.62618, 22.94857);

        mMap.addMarker(new MarkerOptions()
                .snippet("Starting at 20:00")
                .position(startLatLong)
                .title("Rally Start Point")
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.start)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLong, 12f));

        if((!Objects.equals(polyLine, ""))){
            PolylineOptions polylineOptions = new PolylineOptions()
                    .width(14)
                    .color(Color.BLUE)
                    .addAll(PolyUtil.decode(polyLine));

            Polyline polyline = mMap.addPolyline(polylineOptions);
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
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

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
}