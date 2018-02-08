package com.skcc.abtsm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;

import java.util.ArrayList;

/**
 * Created by Jinyoung on 2018-02-06.
 */

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private Marker mPerth;
    private Marker mSydney;
    private Marker mBrisbane;
    private BTS mPerth_BTS;
    private BTS mSydney_BTS;
    private BTS mBrisbane_BTS;
    private int count;

    private GoogleMap mMap;

    private LatLngBounds.Builder builder;
    private LatLngBounds bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, PhotoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Called when the map is ready. */
    @Override
    public void onMapReady(GoogleMap map) {
        BTS BTS_temp;
        Marker Marker_temp;
        mMap = map;
        count = 0;
        builder = new LatLngBounds.Builder();

        ArrayList<BTS> BTSList = new ArrayList<BTS>();
        ArrayList<Marker> MarkerList = new ArrayList<Marker>();
        ClusterManager<BTS> mClusterManager = new ClusterManager<BTS>(this, mMap);


        /*나중에 살릴부분 new BTS에는 JsonArray로 부터 받은 값
        for(int i=0; i<count;i++){
            BTS_temp = new BTS("AB3234D", 37.366386, 127.106660 , 0,
                    "분당구 첫번째", "306호", "2017-08-18", "2018-01-01")
            BTSList.add(BTS_temp);
            mClusterManager.addItem(BTS_temp);

        }*/
        //나중에 지울 부분//////////
        BTSList.add(new BTS("AB3234D", 37.366386, 127.106660 , 0,
                "분당구 첫번째", "306호", "2017-08-18", "2018-01-01"));
        BTSList.add(new BTS("AB3234D", 37.365002, 127.112362 , 0,
                "분당구 두번째", "306호", "2017-08-18", "2018-01-01"));
        BTSList.add(new BTS("AB3234D", 37.374918, 127.116285 , 0,
                "분당구 세번째", "306호", "2017-08-18", "2018-01-01"));

        mClusterManager.addItem(BTSList.get(0));
        mClusterManager.addItem(BTSList.get(1));
        mClusterManager.addItem(BTSList.get(2));
        ////////////////////////////////////////////

        for(int i=0; i<BTSList.size(); i++){
            Marker_temp = mMap.addMarker(new MarkerOptions()
                    .position(BTSList.get(i).getPosition())
                    .title(BTSList.get(i).getStreetAaddress())
                    .snippet(BTSList.get(i).getSsid()));
            MarkerList.add(Marker_temp);
            builder.include(Marker_temp.getPosition());
        }
        bound = builder.build();

        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        //mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            mMap.setMyLocationEnabled(true);
            // Show rationale and request permission.
        }
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bound.getCenter(), 15));
    }


    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
