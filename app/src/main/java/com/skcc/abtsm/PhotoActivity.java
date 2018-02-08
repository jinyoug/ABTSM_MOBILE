package com.skcc.abtsm;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skcc.abstsm.vo.ExifStore;
import com.skcc.abstsm.vo.ImageInfo;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.skcc.abtsm.BuildConfig.DEBUG;


public class PhotoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String fileRealPath = null;

    private static final int PICK_FROM_CAMERA = 1;
    private ImageView imgview;
    Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    public TextView metadataView;

    static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Thumbnails.DATA
    };

    final Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    String szDateTop = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(PhotoActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(PhotoActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        setContentView(R.layout.activity_photo);


        metadataView = (TextView) findViewById(R.id.ImageInfoView);

        String filename = getExternalFilesDir(null) + "/pic.jpg";

        imgview = (ImageView) findViewById(R.id.imageView);
        Button buttonCamera = (Button) findViewById(R.id.button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.test);

        //initialize Tesseract API
        String language = "kor";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, language);

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.d("log1", "onClick-------------------------------------");

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
        });

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
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Log.d("log1", "PICK_FROM_CAMERA___________________--------------");
            Bundle extras = data.getExtras();

            String tempPath = "";

            if (extras != null) {
                Log.d("log1", "extras not null");
                Bitmap photo = extras.getParcelable("data");
                imgview.setImageBitmap(photo);
                image = (Bitmap)extras.get("data");

                getMetadataFromImage();

            }
        }
    }


      public String getRealPathLastImage(){

          Log.d("log1", "Uri " + uriImages.toString());

          try{
              final Cursor cursorImages = getContentResolver().query(uriImages, IMAGE_PROJECTION, null, null, null);
              if(cursorImages != null && cursorImages.moveToLast()){
                  szDateTop = cursorImages.getString(0);
                  cursorImages.close();
                  Log.d("log1", "마지막 사진 경로" + szDateTop);
              }
          }catch(Exception e){
              e.printStackTrace();
              Log.d("log1", "경로 얻기 에러!!!!!!!!!!");
          }
          return szDateTop;
      }

      public void getMetadataFromImage(){
          try {
              ExifStore exifstore = new ExifStore(this);
              ExifInterface exif = new ExifInterface(getRealPathLastImage());
              exifstore.readGeoTagImage(getRealPathLastImage());
              exifstore.showExif(exif);
              Log.d("log1", "날짜 :" + exifstore.getDateTime(exif) + "lat : " + exifstore.getLat(exif) + "lon : " + exifstore.getLon(exif));
          } catch (IOException e) {
              e.printStackTrace();
              Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
          }
      }


    public void processImage(View view){
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRresult);
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
            String filepath = datapath + "/tessdata/kor.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/kor.traineddata");
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


}
