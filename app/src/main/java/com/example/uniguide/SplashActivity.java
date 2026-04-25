package com.example.uniguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Fade in animation
        RelativeLayout root = findViewById(R.id.splashRoot);
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(1000);
        root.startAnimation(anim);

        // Wait 2.5 seconds then check session
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSession();
            }
        }, 2500);
    }

    // SharedPreferences — check if user is already logged in
    // Logged in  → HomeActivity
    // Not logged → LoginActivity
    private void checkSession() {
        SharedPreferences prefs = getSharedPreferences(
            Config.PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(Config.KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
