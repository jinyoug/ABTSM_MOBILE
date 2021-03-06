package com.skcc.abtsm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(Color.parseColor("#bf1517"));

        startActivity(new Intent(this, SplashActivity.class));
    }

    public void onBtnLoginClick(View v){
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }

}
