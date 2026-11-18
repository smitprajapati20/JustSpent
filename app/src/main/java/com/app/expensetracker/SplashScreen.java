package com.app.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sp.getString(ConstantSP.name, "").isEmpty()){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashScreen.this, Dashboard.class));
                    finish();
                }
            }
        }, 3000);
    }
}