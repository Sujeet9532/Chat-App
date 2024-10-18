package com.sujeet.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplaceActivity extends AppCompatActivity {
    ImageView logo;
    TextView name, own1, own2;
    Animation topAnim, bottomAnim;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        logo = findViewById(R.id.logoimg);
        name = findViewById(R.id.logonameimg);
        own1 = findViewById(R.id.ownone);
        own2 = findViewById(R.id.owntwo);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        own1.setAnimation(bottomAnim);
        own2.setAnimation(bottomAnim);

      new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              SharedPreferences shef = getSharedPreferences("login", MODE_PRIVATE);
              boolean check = shef.getBoolean("flag", false);
              if (check) {
                  Intent intent = new Intent(SplaceActivity.this, MainActivity.class);
                  startActivity(intent);
                  finish();

              } else {
                  Intent intent = new Intent(SplaceActivity.this, LoginActivity.class);
                  startActivity(intent);
                  finish();


              }
          }
      },4000);
    }
}