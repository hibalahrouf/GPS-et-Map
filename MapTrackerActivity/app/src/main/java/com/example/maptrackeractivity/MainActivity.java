package com.example.maptrackeractivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private MapView liveMap;
    private LocationManager gpsManager;

    private static final int LOCATION_REQUEST = 101;

    private boolean firstFix = true;

    private final ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        liveMap = findViewById(R.id.openMap);
        liveMap.setMultiTouchControls(true);
        liveMap.getController().setZoom(15.0);

        // Default location (Morocco fallback)
        GeoPoint startPoint = new GeoPoint(33.5731, -7.5898);
        liveMap.getController().setCenter(startPoint);

        gpsManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        checkPermission();
    }

    // ---------------- PERMISSION ----------------
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST
            );

        } else {
            beginTracking();
        }
    }

    // ---------------- START GPS ----------------
    private void beginTracking() {

        if (!gpsManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDialog();
            return;
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        gpsManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5,
                this
        );


        if (gpsManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            gpsManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    5,
                    this
            );
        }

        Toast.makeText(this, "Tracking enabled", Toast.LENGTH_SHORT).show();
    }
    // ---------------- GPS DIALOG ----------------
    private void showGpsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("GPS Disabled")
                .setMessage("Please enable GPS to track your position")
                .setPositiveButton("Enable", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- LOCATION UPDATE ----------------
    @Override
    public void onLocationChanged(@NonNull Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        GeoPoint userPoint = new GeoPoint(lat, lng);

        // ---------------- MARKER ----------------
        Marker marker = new Marker(liveMap);
        marker.setPosition(userPoint);
        marker.setTitle("Current Position");

        markers.add(marker);
        liveMap.getOverlays().add(marker);

        // Keep only last 20 markers (prevents memory crash)
        if (markers.size() > 20) {
            liveMap.getOverlays().remove(markers.get(0));
            markers.remove(0);
        }

        // ---------------- CAMERA CONTROL ----------------
        if (firstFix) {
            liveMap.getController().setZoom(18.0);
            liveMap.getController().setCenter(userPoint);
            firstFix = false;
        } else {
            liveMap.getController().animateTo(userPoint);
        }

        liveMap.postInvalidate();

        Toast.makeText(this,
                lat + " , " + lng,
                Toast.LENGTH_SHORT).show();
    }

    // ---------------- PERMISSIONS RESULT ----------------
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beginTracking();
            }
        }
    }

    // ---------------- CLEANUP ----------------
    @Override
    protected void onPause() {
        super.onPause();
        if (gpsManager != null) {
            gpsManager.removeUpdates(this);
        }
    }
}