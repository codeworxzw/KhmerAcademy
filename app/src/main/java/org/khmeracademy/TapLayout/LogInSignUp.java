package org.khmeracademy.TapLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.khmeracademy.Activity.MainCategory;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

@SuppressLint("ValidFragment")
public class LogInSignUp extends Fragment implements LinkButtonToTab {
    private String mTitle;

    @NotEmpty(messageId = org.khmeracademy.R.string.validation_empty)
    private EditText edFullName;

    @NotEmpty(messageId = org.khmeracademy.R.string.validation_empty)
    @RegExp(value = EMAIL, messageId = org.khmeracademy.R.string.validation_valid_email)
    private EditText edEmail;

    @NotEmpty(messageId = org.khmeracademy.R.string.validation_empty)
    private EditText edPassword;

    private EditText edComfirmPassword;
    private Button btnSignUp;
    private Button btnLogIn;
    private Spinner spGender;
    private SharedPreferences.Editor sessionEditor;
    private String gender;

    public static LogInSignUp getInstance(String title) {
        LogInSignUp sf = new LogInSignUp();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstadnceState) {
        View v;
        if(mTitle.equals("Sign Up")){
            v = inflater.inflate(org.khmeracademy.R.layout.activity_sign_up, container, false);
            edFullName = (EditText) v.findViewById(org.khmeracademy.R.id.editReFullName);
            edPassword = (EditText) v.findViewById(org.khmeracademy.R.id.editRePassword);
            edEmail = (EditText) v.findViewById(org.khmeracademy.R.id.editReEmailAddress);
            edComfirmPassword = (EditText) v.findViewById(org.khmeracademy.R.id.editReConfirmPassword);
            spGender = (Spinner) v.findViewById(org.khmeracademy.R.id.spinnerGender);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), org.khmeracademy.R.array.gender_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spGender.setAdapter(adapter);
            spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //if(spGender.getSelectedItem().equals(""))
                    if(spGender.getSelectedItemPosition()==0){
                        //Toast.makeText(view.getContext(), "Male", Toast.LENGTH_SHORT).show();
                        gender = "Male";
                    }else {
                        //Toast.makeText(view.getContext(), "Female", Toast.LENGTH_SHORT).show();
                        gender="Female";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            btnSignUp = (Button) v.findViewById(org.khmeracademy.R.id.btn_sign_up);
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SlidingTabLayout tabLayout_2 = (SlidingTabLayout) container.findViewById(R.id.tl_2);
                    //Toast.makeText(getContext(), "onTabSelect&position--->" + tabLayout_2.getChildCount(), Toast.LENGTH_SHORT).show();
                    //calling validation
                    validateSignUp();
                }
            });
        }
        else{
            v = inflater.inflate(org.khmeracademy.R.layout.activity_log_in, container, false);
            btnLogIn = (Button) v.findViewById(org.khmeracademy.R.id.btn_log_in);
            edEmail = (EditText) v.findViewById(org.khmeracademy.R.id.editInEmail);
            edPassword = (EditText) v.findViewById(org.khmeracademy.R.id.editInPassword);
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //calling validation
                    validateLogin();
                }
            });
        }
        return v;
    }
    @Override
    public void selectTab(int tabNum) {

    }
    private void validateLogin(){
        if(FormValidator.validate(this, new SimpleErrorPopupCallback(getContext()))){
            logIn();
        }
        FormValidator.startLiveValidation(this, new SimpleErrorPopupCallback(getContext()));
        FormValidator.stopLiveValidation(this);
    }

    private void validateSignUp(){
        if(FormValidator.validate(this, new SimpleErrorPopupCallback(getContext()))){
            signUp();
        }
        FormValidator.startLiveValidation(this, new SimpleErrorPopupCallback(getContext()));
        FormValidator.stopLiveValidation(this);
    }

    private void logIn(){
        JSONObject param = new JSONObject();
        try {
            param.put("email",edEmail.getText().toString());
            param.put("password", edPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final SpotsDialog pDialog = new SpotsDialog(getActivity(), "កំពុងដំណើរការ");
        pDialog.show();


        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.logInUrl, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // If logIn Successfully
                    if (response.getBoolean("STATUS")){
                        sessionEditor = getActivity().getSharedPreferences("userSession",0).edit();
                        sessionEditor.putBoolean("isLogin", true);
                        sessionEditor.putString("id", response.getString("USERID"));
                        sessionEditor.putString("profile_picture", response.getString("PROFILE_IMG_URL"));
                        sessionEditor.putString("cover_picture", response.getString("COVER_IMG_URL"));
                        sessionEditor.putString("userName", response.getString("USERNAME"));
                        sessionEditor.putString("email", response.getString("EMAIL"));
                        sessionEditor.apply();
                        Intent intent = new Intent(getActivity(), MainCategory.class);
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Toast.makeText(getActivity(), "Invalid email or password!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getActivity(), org.khmeracademy.R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void signUp(){
        JSONObject param = new JSONObject();

        try {
            param.put("email",edEmail.getText().toString());
            param.put("password", edPassword.getText().toString());
            param.put("username", edFullName.getText().toString());
            param.put("gender", gender);
            param.put("universityId","MQ==");
            param.put("departmentId","MQ==");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final SpotsDialog pDialog = new SpotsDialog(getActivity(),  "កំពុងដំណើរការ");
        pDialog.show();

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.signUpUrl, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                ((LinkButtonToTab) getActivity()).selectTab(0);
                Toast.makeText(getActivity(), "ចុះឈ្មោះដោយជោគជ័យ", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Please Check Internet Connection !!", Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }
}