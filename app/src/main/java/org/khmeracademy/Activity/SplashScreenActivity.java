package org.khmeracademy.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.khmeracademy.AppIntro.AppIntroMain;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences session;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.khmeracademy.R.layout.activity_flash_screen);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                //here you can start your Activity B.
                //Intent i=new Intent(SplashScreenActivity.this,MainActivity.class);
                // setContentView(R.layout.activity_main);
                session = getSharedPreferences("userSession", Context.MODE_PRIVATE);
                if(session.getBoolean("isLogin",false)){
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
