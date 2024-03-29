package org.khmeracademy.AppIntro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.github.paolorotolo.appintro.AppIntro2;
import org.khmeracademy.Activity.RegisterActivity;
import org.khmeracademy.R;

/**
 * Created by sok-ngim on 12/28/15.
 */
public class AppIntroMain extends AppIntro2 {
    public static final String SETTING = "firstLaunch";
    @Override
    public void init(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        if (prefs.getBoolean("APP", false) == true) {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
            finish();
        }

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));

        setIndicatorColor(Color.parseColor("#5F6B6F"), Color.parseColor("#AAB0B2"));
        setProgressButtonEnabled(false);
    }
    private void loadLoginActivity(){
        startApp();
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void startApp(){
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("APP", true);
        editor.apply();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        loadLoginActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    public void StartLearning(View v){
       loadLoginActivity();
    }
}
