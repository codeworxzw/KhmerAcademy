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


public class MainActivity extends AppCompatActivity {
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Session.isLogin) {
            i = new Intent(this, MainCategory.class);
        } else {
            i = new Intent(this, AppIntroMain.class);
        }
        startActivity(i);
        finish();
    }
}
