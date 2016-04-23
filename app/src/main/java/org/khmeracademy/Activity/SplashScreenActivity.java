package org.khmeracademy.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.khmeracademy.AppIntro.AppIntroMain;
import org.khmeracademy.Util.Session;
import org.khmeracademy.Util.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.khmeracademy.R.layout.activity_flash_screen);

        if (Utility.isNetworkAvailable(this)) {
            Log.i("ooooo", "True");
        } else {
            Log.i("ooooo", "False");
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                //here you can start your Activity B.
                //Intent i=new Intent(SplashScreenActivity.this,MainActivity.class);
                // setContentView(R.layout.activity_main);
                Session.readUserSession(SplashScreenActivity.this);
                if(Session.isLogin){
                    i = new Intent(SplashScreenActivity.this, MainCategory.class);
                }else{
                    i = new Intent(SplashScreenActivity.this, AppIntroMain.class);
                }
                finish();
                startActivity(i);

            }

        }, 1500);
    }
}
