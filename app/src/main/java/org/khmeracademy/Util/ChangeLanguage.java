package org.khmeracademy.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Manith on 3/13/2016.
 */
public class ChangeLanguage {
    public ChangeLanguage(Context context) {
        Locale locale = null;
        Configuration config = null;

        SharedPreferences sharedPref = context.getSharedPreferences("setting",
                Context.MODE_PRIVATE);

        if (locale == null) locale = new Locale(sharedPref.getString("LANGUAGE", "km"));
        Locale.setDefault(locale);
        if (config == null) config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }
}
