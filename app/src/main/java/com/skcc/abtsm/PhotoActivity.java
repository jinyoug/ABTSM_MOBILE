package com.skcc.abtsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skcc.abstsm.vo.BTS;
import com.skcc.abstsm.vo.ExifStore;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int PICK_FROM_CAMERA = 1;
    private static final String userID = "09801";
    private static final String language = "eng";
    private int status=0;
    private String ResponseMsg;
    private ImageView imgview;
    private TessBaseAPI mTess;
    private Bitmap image;
    private String datapath = null;
    private List<Address> list = null;
    private String OCRresult = null;
    private String btsjson = null;
    public String strJson = null;
    public TextView metadataView;
    public BTS mBTS;

    private EditText etBtsId;
    private EditText etAddress;
    private EditText etAddressDetail;
    private EditText etLatitude;

    private final Geocoder geocoder = new Geocoder(this);
    static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Thumbnails.DATA
    };

    final Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    String szDateTop = "";
    public TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Permission Listener Start
         */
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(PhotoActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(PhotoActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
        /*
        Permission Listener End
         */

        setContentView(R.layout.activity_photo);

        metadataView = (TextView) findViewById(R.id.ImageInfoView);
        imgview = (ImageView) findViewById(R.id.imageView);

        //Button Listener
        ImageButton buttonCamera = (ImageButton) findViewById(R.id.btn_camera);
        Button buttonSend = (Button)findViewById(R.id.btn_send);
        tvResponse = (TextView)findViewById(R.id.OCRTextView);
        buttonCamera.setOnClickListener(this);
        buttonSend.setOnClickListener(this);


        etBtsId= (EditText)findViewById(R.id.et_bts_id);
        etAddress= (EditText)findViewById(R.id.et_address);
        etAddressDetail= (EditText)findViewById(R.id.et_address_detail);
        etLatitude= (EditText)findViewById(R.id.et_latitude);

        /*
        * ActionBar 동작
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // check if you are connected or not
        if(isConnected()){
            Toast.makeText(this, "You are connected", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "You are disconnected", Toast.LENGTH_LONG).show();
        }

        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.test);

        /*
        initialize Tesseract API
        */
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, language);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this,PhotoActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, SearchActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.nav_send){
            Intent intent = new Intent(this, SendActivity.class);
            finish();
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BTS bts;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                imgview.setImageBitmap(photo);
                image = (Bitmap)extras.get("data");

                getMetadataFromImage();             //Meta Data 읽기 > 메소드 내에서 mBTS에 값 Setting
                mBTS.setSsid(processImage());       //TODO : ID 읽어오는 코드 수정 요망 - 필수는 아니니까
                btsjson = mBTS.toString();

                etBtsId.setText(mBTS.getSsid());
                etAddress.setText(mBTS.getStreetAaddress());
                etLatitude.setText(mBTS.getLatitude() + "/"+mBTS.getLongitude());

                Log.i("gson",btsjson);
            }
        }
    }


    private String processImage(){
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        return OCRresult;
    }

      public String getRealPathLastImage(){

          try{
              final Cursor cursorImages = getContentResolver().query(uriImages, IMAGE_PROJECTION, null, null, null);
              if(cursorImages != null && cursorImages.moveToLast()){
                  szDateTop = cursorImages.getString(0);
                  cursorImages.close();
              }
          }catch(Exception e){
              e.printStackTrace();
          }
          return szDateTop;
      }

      public BTS getMetadataFromImage(){
          String date = null;
          String streetAaddress = null;
          double lat = 0;
          double lon = 0;
          try {
              ExifStore exifstore = new ExifStore(this);
              ExifInterface exif = new ExifInterface(getRealPathLastImage());
              exifstore.readGeoTagImage(getRealPathLastImage());
              exifstore.showExif(exif);
              date = exifstore.getDateTime(exif);
              lat =  exifstore.getLat(exif);
              lon = exifstore.getLon(exif);
              streetAaddress = ReverseGeocoding(lat,lon);
              mTess.setImage(image);
              OCRresult = mTess.getUTF8Text();

              mBTS = new BTS(userID,OCRresult, lat, lon, 0, ReverseGeocoding(lat,lon), "상세주소", exif.getAttribute(ExifInterface.TAG_DATETIME), exif.getAttribute(ExifInterface.TAG_DATETIME));
              Log.d("BTS", "SSID :" + mBTS.getSsid() +"날짜 :" + mBTS.getEnrollDate() + "lat : " + mBTS.getLatitude() + "lon : " + mBTS.getLongitude() +"주소"+ mBTS.getStreetAaddress());
          } catch (IOException e) {
              e.printStackTrace();
              Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
          }
          return mBTS;
      }

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/kor.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String ReverseGeocoding(double v1, double v2) throws IOException {
        try {
            list = geocoder.getFromLocation(
                    v1, // 위도
                    v2, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if(list != null){
            if(list.size() == 0){return null;}
            else{
                return list.get(0).getAddressLine(0);
            }
        }else{return null;}
    }

    private boolean validate(){
          //유효성 검사
            return true;
    }

    @Override
    public void onClick(View view) {
        JSONArray json = null;
        JSONObject jobject = null;
        int status = 0;
        switch(view.getId()){
            case R.id.btn_send:

                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                else {
                    HttpAsyncTask httpTask = new HttpAsyncTask(PhotoActivity.this);
                    mBTS.setSsid(etBtsId.getText().toString());
                    mBTS.setStreetAaddress(etAddress.getText().toString());
                    mBTS.setSecondaryUnit(etAddressDetail.getText().toString());
                    btsjson = mBTS.toString();
                    Log.i("Send-userID",userID);
                    Log.i("Send-btsjson",btsjson);
                    httpTask.execute("http://abtsm-be.paas.sk.com/bts/d1/enroll/" + userID, btsjson);
                }
                break;
            case R.id.btn_camera:
                // call the camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

                // image size for cutting
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);

                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private  PhotoActivity mainAct;

        HttpAsyncTask(PhotoActivity mainActivity) {
            this.mainAct = mainActivity;
        }
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],urls[1]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ResponseMsg = result;       //  POST() 를 통해 Return 받은 string(Log.i("Return Result")와 동일)
            Log.i("RESPONSE", ResponseMsg);
            mainAct.runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    //TODO : Return MSG 확인 > BackEnd 현재 올라간 버전 확인 후 ReturnMSG에 따라 처리 진행 일단 다른 SearchActivity로 이동
                    Intent intent = new Intent(mainAct,SearchActivity.class);
                    finish();
                    startActivity(intent);
//                    Toast.makeText(mainAct, "Received!", Toast.LENGTH_LONG).show();
//                    try {
//                        JSONObject json = new JSONObject(ResponseMsg);
//                        status = json.getInt("status");
//                        Log.i("Json Info ! ", json.toString());
//                        Log.i("Json Info2 ! ", json.toString(1));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    finally {
//                        if(status == 200){
//                            Toast.makeText(getBaseContext(), "Duplicated BTS ID", Toast.LENGTH_LONG).show();
//                            Intent intent = getIntent();
//                            finish();
//                            startActivity(intent);
//                        }
//                    }
                }
            });

        }
        protected String POST(String url, String btsjson){
            InputStream is;
            String result = null;
            try {
                URL urlCon = new URL(url);
                HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();
                httpCon.setRequestMethod("POST");

                // Set some headers to inform server about the type of the content
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setRequestProperty("Content-type", "application/json; charset=utf-8");

                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                httpCon.setDoOutput(true);
                // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
                httpCon.setDoInput(true);

                httpCon.connect();

                OutputStream os = httpCon.getOutputStream();
                Log.d("BTSJSON",btsjson);
                os.write(btsjson.getBytes("utf-8"));
                os.flush();

                result = "NORMAL";
                Log.i("Response Code",httpCon.getResponseCode()+"");
                Log.i("Response Message",httpCon.getResponseMessage()+"");
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
