package org.khmeracademy.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.khmeracademy.AppIntro.AppIntroMain;
import org.khmeracademy.Util.Utility;


public class MainActivity extends AppCompatActivity {
    SharedPreferences session;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utility.isNetworkAvailable(this)){
            Log.i("ooooo", "True");
        }else{
            Log.i("ooooo", "False");
        }
       // setContentView(R.layout.activity_main);
        session = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        if(session.getBoolean("isLogin",false)){
            i = new Intent(this, MainCategory.class);
        }else{
            i = new Intent(this, AppIntroMain.class);
        }
        startActivity(i);
        finish();
    }
}
