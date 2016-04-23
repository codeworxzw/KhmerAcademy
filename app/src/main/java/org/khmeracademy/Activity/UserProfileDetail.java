package org.khmeracademy.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.R;
import org.khmeracademy.Util.ChangeLanguage;
import org.khmeracademy.Util.CustomDialog;
import org.khmeracademy.Util.MyNavigationDrawer;

import org.json.JSONException;
import org.json.JSONObject;
import org.khmeracademy.Util.Session;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Manith on 12/19/2015.
 */
public class UserProfileDetail extends AppCompatActivity {

    //private MenuItem menuItemProfile;
    private TextView username;
    private TextView email;
    private TextView gender;
    private TextView dateOfBirth;
    private TextView department;
    private TextView university;
    private TextView phone;
    private MyNavigationDrawer nvd;
    private CircleImageView imageProfile;
    String imagePath;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ChangeLanguage(this);

        setContentView(R.layout.activity_user_profile);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.userPrfile_title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));

        // Change from Navigation menu item image to arrow back image of toolbar
        toolbar.setNavigationIcon(R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Call navigation drawer
        nvd = new MyNavigationDrawer(this, R.id.nav_view);
        try {
            requestResponse(Session.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Initialize layout userProfileDetail objects
        initView();

    }

    private void initView() {
        username= (TextView) findViewById(R.id.user_profile_name);
        email = (TextView)findViewById(R.id.user_profile_email);
        gender = (TextView)findViewById(R.id.user_profile_gender);
        dateOfBirth = (TextView)findViewById(R.id.user_profile_dateOfBirth);
        department = (TextView)findViewById(R.id.user_profile_department);
        university = (TextView)findViewById(R.id.user_profile_university);
        phone = (TextView)findViewById(R.id.user_profile_phone_number);
        imageProfile = (CircleImageView)findViewById(R.id.profileImage);
    }

    // Call to refresh navigation header
    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            nvd.requestInfoNav();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Load image from server
    public void requestResponse(String id) throws JSONException {
        String url = API.profileDetail + "/" + id;

        CustomDialog.showProgressDialog(this);
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("ooooo", response.getJSONObject("RES_DATA").toString());
                    if (response.getBoolean("STATUS")) {
                        JSONObject object = response.getJSONObject("RES_DATA");
                        username.setText(object.getString("username"));
                        email.setText(object.getString("email"));
                        gender.setText(object.getString("gender"));
                        if(gender.getText().toString().toLowerCase().equals("male")){
                            gender.setText("ប្រុស");
                        }else {
                            gender.setText("ស្រី");
                        }
                        dateOfBirth.setText(object.getString("dateOfBirth").equals("null") ? "N/A" : object.getString("dateOfBirth"));
                        department.setText(object.getString("departmentName").equals("null") ? "N/A" : object.getString("departmentName"));
                        university.setText(object.getString("universityName").equals("null") ? "N/A" : object.getString("universityName"));
                        phone.setText(object.getString("phoneNumber").equals("null") ? "N/A" : object.getString("phoneNumber"));
                        imagePath = object.getString("userImageUrl");

                        //String imgProfile = API.BASE_URL + "/resources/upload/file/"+ imagePath;
                        Picasso.with(getApplicationContext())
                                .load(imagePath)
                                .placeholder(R.drawable.icon_user)
                                .error(R.drawable.icon_user).into(imageProfile);
                    } else {
                        Toast.makeText(UserProfileDetail.this, "Invalid User Id", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    CustomDialog.hideProgressDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.hideProgressDialog();
                Toast.makeText(UserProfileDetail.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Profile Detail");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.recreate();
    }
}
