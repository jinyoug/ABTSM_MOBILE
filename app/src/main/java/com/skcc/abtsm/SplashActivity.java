package com.skcc.abtsm;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
/**
 * Created by Jinyoung on 2018-02-09.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
