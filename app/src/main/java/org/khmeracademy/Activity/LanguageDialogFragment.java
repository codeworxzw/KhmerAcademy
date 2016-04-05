package org.khmeracademy.Activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import org.khmeracademy.R;
import org.khmeracademy.Util.Setting;

import java.util.Locale;

public class LanguageDialogFragment extends DialogFragment {
    private Spinner mLanguageSpinner;
    private Button btnOk;
    private View view;
    private int oldLanguage;
    private boolean oldScrollAnimationStatus;
    private Button btn_eng;
    private Button btn_khm;
    private Button btn_cancel;
    private String defaultLamguage="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Check if user changed language
        Setting.loadLanguage(getActivity());

        Setting.readSetting(getActivity());
        defaultLamguage = Setting.LANGUAGE;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        Locale locale = null;
        Configuration config = null;

        if (locale == null) locale = new Locale(sharedPref.getString("LANGUAGE", "km"));
        Locale.setDefault(locale);
        if (config == null) config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());
        /*getDialog().setTitle(getString(R.string.nav_setting_lan));
        getDialog().getWindow().setGravity(Gravity.CENTER);*/


       // setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL)
        View view = inflater.inflate(R.layout.fragment_language_dialog, container, false);

        initWidgets(view);

        if (defaultLamguage.equals(Setting.languages[1])){
            btn_khm.setEnabled(false);
            btn_khm.setBackgroundColor(getResources().getColor(org.khmeracademy.R.color.colorgray));
           // btn_khm.setBackgroundDrawable(getResources().getDrawable(org.khmeracademy.R.drawable.shape_edit_text_style_border_disable));
        }
        else {
            btn_eng.setEnabled(false);
            btn_eng.setBackgroundColor(getResources().getColor(org.khmeracademy.R.color.colorgray));
        }
        return view;
    }

    public void change(String language){

        SharedPreferences sharedPref = getActivity().getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        Locale locale = null;
        Configuration config = null;

        locale = new Locale(language);
        Locale.setDefault(locale);
        config = new Configuration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        /*Intent intent = new Intent(getActivity(), MainCategory.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
        //getActivity().finish();

        //Toast.makeText(getActivity(), "Locale in Khmer !", Toast.LENGTH_LONG).show();

        Setting.save(getActivity(), language);

        getActivity().onConfigurationChanged(config);
    }

    public void initWidgets(View view) {
        btn_eng = (Button) view.findViewById(R.id.btnEn);
        btn_khm = (Button) view.findViewById(R.id.btnKh);
        btn_cancel = (Button) view.findViewById(R.id.btnCancel);

        if (defaultLamguage.equals(Setting.languages[1])){
            btn_khm.setEnabled(false);
        }

        else {
            btn_eng.setEnabled(true);
        }

       // oldLanguage = mLanguageSpinner.getSelectedItemPosition();
        //
        initEvent();
    }

    public void initEvent() {
        btn_khm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String x = Setting.languages[1];
                change(x);
                dismiss();
            }
        });

        btn_eng.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String x = Setting.languages[0];
                change(x);
                dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
