package com.skcc.abtsm;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.skcc.abstsm.vo.BTS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jinyoung on 2018-02-06.
 */

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private static final String userID = "09801";

    private String ResponseMsg;

    private GoogleMap mMap;

    private LatLngBounds.Builder builder;
    private LatLngBounds bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SearchActivity.HttpAsyncTask httpTask = new SearchActivity.HttpAsyncTask(SearchActivity.this);
        httpTask.execute("http://abtsm-be.paas.sk.com/bts/d1/my/"+ userID, null);

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
        String ssid = null;
        Double latitude = null;
        Double longitude = null;
        int altitude = 0;
        String streetAddress = null;
        String secondaryUnit = null;
        String enrollDate = null;
        String modifyDate = null;

        Marker Marker_temp;
        BTS BTS_temp;
        JSONArray json = null;
        JSONObject jobject = null;
        mMap = map;
        builder = new LatLngBounds.Builder();

        ArrayList<BTS> BTSList = new ArrayList<BTS>();
        ArrayList<Marker> MarkerList = new ArrayList<Marker>();
        ClusterManager<BTS> mClusterManager = new ClusterManager<BTS>(this, mMap);
        Log.d("JSONMSG", ResponseMsg);

        try {
            json = new JSONArray(ResponseMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*나중에 살릴부분 new BTS에는 JsonArray로 부터 받은 값*/
        for(int i=0; i<json.length();i++){
            try {
                jobject = json.getJSONObject(i);
                ssid = jobject.getString("ssid");
                latitude = jobject.getDouble("latitude");
                longitude = jobject.getDouble("longitude");
                altitude = jobject.getInt("altitude");
                streetAddress = jobject.getString("streetAddress");
                secondaryUnit = jobject.getString("secondaryUnit");
                enrollDate = jobject.getString("enrollDate");
                modifyDate = jobject.getString("modifyDate");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BTS_temp = new BTS("09801",ssid, latitude, longitude , altitude,
                    streetAddress, secondaryUnit, enrollDate, modifyDate);
            BTSList.add(BTS_temp);
            mClusterManager.addItem(BTS_temp);

        }
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
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            mMap.setMyLocationEnabled(true);
            // Show rationale and request permission.
        }
        */
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

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private  SearchActivity mainAct;

        HttpAsyncTask(SearchActivity mainActivity) {
            this.mainAct = mainActivity;
        }
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ResponseMsg = result;       //  POST() 를 통해 Return 받은 string(Log.i("Return Result")와 동일)
            Log.i("RESPONSE", ResponseMsg);
            mainAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainAct, "Received!", Toast.LENGTH_LONG).show();
                    try {
                        JSONArray json = new JSONArray(ResponseMsg);
                        Log.i("Json Info ! ", json.toString());
                        Log.i("Json Info2 ! ", json.toString(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        protected String POST(String url){
            InputStream is;
            String result = null;
            try {
                URL urlCon = new URL(url);
                HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

                // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);


                // Set some headers to inform server about the type of the content
                httpCon.setRequestProperty("User-Agent", "my-rest-app-v0.1");

                result = "NORMAL";

                // receive response as inputStream
                try {
                    is = httpCon.getInputStream();
                    // convert inputstream to string
                    if(is != null){
                        result = convertInputStreamToString(is);
                        Log.d("PLEASE3",result);
                    }
                    else {
                        result = "Did not work!";
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    httpCon.disconnect();
                }

            }
            catch (IOException e) {
                result = "FAIL";
                e.printStackTrace();
            }

            catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            Log.i("Return Result ! ", result);
            return result;
        }

        protected String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;
        }

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
