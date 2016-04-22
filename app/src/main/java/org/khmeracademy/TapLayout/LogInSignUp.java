package org.khmeracademy.TapLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.khmeracademy.Activity.MainCategory;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.khmeracademy.R;

import dmax.dialog.SpotsDialog;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

@SuppressLint("ValidFragment")
public class LogInSignUp extends Fragment implements LinkButtonToTab {
    private String mTitle;

    @NotEmpty(messageId = R.string.validation_empty, order = 1)
    private EditText edFullName;

    @NotEmpty(messageId = R.string.validation_empty, order = 2)
    @RegExp(value = EMAIL, messageId = R.string.validation_valid_email, order = 3)
    private EditText edEmail;

    @NotEmpty(messageId = R.string.validation_empty, order = 4)
    private EditText edPassword;

    private EditText edComfirmPassword;
    private Button btnSignUp;
    private Button btnLogIn;
    private Spinner spGender;
    private SharedPreferences.Editor sessionEditor;
    private String gender;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    public static LogInSignUp getInstance(String title) {
        LogInSignUp sf = new LogInSignUp();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstadnceState) {
        // FB Initialize
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        View v;
        if(mTitle.equals("Sign Up")){
            v = inflater.inflate(R.layout.activity_sign_up, container, false);
            edFullName = (EditText) v.findViewById(R.id.editReFullName);
            edPassword = (EditText) v.findViewById(R.id.editRePassword);
            edEmail = (EditText) v.findViewById(R.id.editReEmailAddress);
            edComfirmPassword = (EditText) v.findViewById(R.id.editReConfirmPassword);
            spGender = (Spinner) v.findViewById(R.id.spinnerGender);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spGender.setAdapter(adapter);
            spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spGender.getSelectedItemPosition() == 0) {
                        gender = "Male";
                    } else {
                        gender = "Female";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            btnSignUp = (Button) v.findViewById(R.id.btn_sign_up);
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
            v = inflater.inflate(R.layout.activity_log_in, container, false);
            btnLogIn = (Button) v.findViewById(R.id.btn_log_in);
            edEmail = (EditText) v.findViewById(R.id.editInEmail);
            edPassword = (EditText) v.findViewById(R.id.editInPassword);
            fbLoginButton = (LoginButton) v.findViewById(R.id.fb_login_button);
            fbLoginButton.setFragment(this);
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //calling validation
                    validateLogin();
                }
            });

            fbLoginButton.setReadPermissions("email", "public_profile", "user_birthday");
            fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest
                            (loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        if (object.has("email")){
                                            String fbId = object.getString("id");
                                            String userName = object.getString("name");
                                            String email = object.getString("email");
                                            String gender = object.getString("gender");
                                            String birthday = object.getString("birthday");

                                            JSONObject jsonObjectPicture = object.getJSONObject("picture");
                                            JSONObject jsonObjectData = jsonObjectPicture.getJSONObject("data");
                                            String imageUrl = jsonObjectData.getString("url");
                                            checkFacebookAccount(email, userName, gender, imageUrl, fbId);
                                        }else{
                                            Toast.makeText(getActivity(), "Login with FB, E-mail required !", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large){url}");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log.d("ooooo", "On cancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("ooooo", error.toString());
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
            if (edComfirmPassword.getText().toString().equals(edPassword.getText().toString())){
                signUp();
            }else{
                edPassword.setError(getString(R.string.password_not_match));
                edComfirmPassword.setError(getString(R.string.password_not_match));
            }
        }
        FormValidator.startLiveValidation(this, new SimpleErrorPopupCallback(getContext()));
        FormValidator.stopLiveValidation(this);
    }

    private void logIn(){
        JSONObject param = new JSONObject();
        try {
            param.put("email", edEmail.getText().toString());
            param.put("password", edPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final SpotsDialog pDialog = new SpotsDialog(getActivity(), getString(R.string.spotDialog));
        pDialog.show();


        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.logInUrl, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // If logIn Successfully
                    if (response.getBoolean("STATUS")){
                        JSONObject jsonObject = response.getJSONObject("USER");
                        sessionEditor = getActivity().getSharedPreferences("userSession",0).edit();
                        sessionEditor.putBoolean("isLogin", true);
                        sessionEditor.putString("id", jsonObject.getString("userId"));
                        sessionEditor.putString("gender", jsonObject.getString("gender"));
                        sessionEditor.putString("profile_picture", jsonObject.getString("userImageUrl"));
                        sessionEditor.putString("cover_picture", jsonObject.getString("userImageUrl"));
                        sessionEditor.putString("userName", jsonObject.getString("username"));
                        sessionEditor.putString("email", jsonObject.getString("email"));
                        sessionEditor.apply();
                        Intent intent = new Intent(getActivity(), MainCategory.class);
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
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
        final SpotsDialog pDialog = new SpotsDialog(getActivity(), getString(R.string.spotDialog));
        pDialog.show();

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.signUpUrl, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                edFullName.setText("");
                edEmail.setText("");
                edPassword.setText("");
                edComfirmPassword.setText("");
                ((LinkButtonToTab) getActivity()).selectTab(0);
                Toast.makeText(getActivity(), R.string.verify_email, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void checkFacebookAccount(final String email, final String userName, String gender, final String imageUrl, String fbId){

        JSONObject param = new JSONObject();
        try {
            param.put("email", email);
            param.put("password", null);
            param.put("username", userName);
            param.put("gender", gender);
            param.put("universityId", null);
            param.put("departmentId", null);
            param.put("imageUrl", imageUrl);
            param.put("scID", fbId);
            param.put("scType", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final SpotsDialog pDialog = new SpotsDialog(getActivity(), getString(R.string.spotDialog));
        pDialog.show();

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.fbLoginUrl, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                try {
                    // If logIn Successfully
                    if (response.getBoolean("STATUS")){
                        JSONObject jsonObject = response.getJSONObject("USER");

                        // Save user session
                        sessionEditor = getActivity().getSharedPreferences("userSession",0).edit();
                        sessionEditor.putBoolean("isLogin", true);
                        sessionEditor.putString("id", jsonObject.getString("userId"));
                        sessionEditor.putString("gender", jsonObject.getString("gender"));
                        sessionEditor.putString("profile_picture", imageUrl);
                        sessionEditor.putString("cover_picture", imageUrl);
                        sessionEditor.putString("userName", userName);
                        sessionEditor.putString("email", email);
                        sessionEditor.apply();

                        Intent intent = new Intent(getActivity(), MainCategory.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}