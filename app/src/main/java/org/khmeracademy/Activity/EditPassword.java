package org.khmeracademy.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Manith on 12/28/2015.
 */
public class EditPassword extends AppCompatActivity{

    private Tracker mTracker;
    private Toolbar toolbar;
    private MyNavigationDrawer nvd;
    private CircleImageView imageProfile;
    String imagePath;
    TextView et_current_password, et_new_password, et_confirm_password;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ChangeLanguage(this);

        setContentView(org.khmeracademy.R.layout.activity_edit_password);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        toolbar = (Toolbar) findViewById(org.khmeracademy.R.id.toolbar_main);
        toolbar.setTitle(org.khmeracademy.R.string.editPwd_title);
        toolbar.setTitleTextColor(getResources().getColor(org.khmeracademy.R.color.textColor));

        // Change from Navigation menu item image to arrow back image of toolbar
        toolbar.setNavigationIcon(org.khmeracademy.R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(org.khmeracademy.R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Call navigation drawer
        nvd = new MyNavigationDrawer(this, org.khmeracademy.R.id.nav_view);

        // Initialize layout edit-password objects
        imageProfile = (CircleImageView) findViewById(org.khmeracademy.R.id.pw_profile_image);
        et_current_password = (TextView) findViewById(org.khmeracademy.R.id.editCurrentPwd);
        et_new_password = (TextView) findViewById(org.khmeracademy.R.id.editNewPwd);
        et_confirm_password = (TextView) findViewById(org.khmeracademy.R.id.editConfirmPwd);

        user_id = getSharedPreferences("userSession", 0).getString("id", "N/A");
        /*try {
            requestResponse(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

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

    // Disable search menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(org.khmeracademy.R.id.action_search1).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.khmeracademy.R.menu.activity_sub_category, menu);
        return true;
    }

    // Save item event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String current_pw = et_current_password.getText().toString();
        String new_pw = et_new_password.getText().toString();
        String confirm_pw = et_confirm_password.getText().toString();
        switch (item.getItemId()){
            case org.khmeracademy.R.id.action_save:
                if (current_pw.equals("") || new_pw.equals("") || confirm_pw.equals("")){
                    Toast.makeText(EditPassword.this, "សូមបំពេញពត៌មានអោយបានត្រឹមត្រូវ", Toast.LENGTH_SHORT).show();
                }else{
                    if (new_pw.equals(confirm_pw)){
                        updatePassword(user_id, current_pw, new_pw);
                    }else {
                        Toast.makeText(EditPassword.this, "ពាក្យសម្ងាត់ថ្មីមិនត្រូវគ្នា", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Load image from server
    public void requestResponse(String id) throws JSONException {
        String url = API.profileDetail + "/" + id;
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("STATUS")) {
                        JSONObject object = response.getJSONObject("RES_DATA");
                        imagePath = object.getString("userImageUrl");
                        final String imgProfile = API.BASE_URL + "/resources/upload/file/"+ imagePath;
                        Picasso.with(getApplicationContext())
                                .load(imgProfile)
                                .placeholder(org.khmeracademy.R.drawable.icon_user)
                                .error(org.khmeracademy.R.drawable.icon_user).into(imageProfile);
                    } else {
                        Toast.makeText(EditPassword.this, "Invalid User Id", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditPassword.this, "There is Something Wrong !!", Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void updatePassword(final String user_id, final String old_password, final String new_password){
        CustomDialog.showProgressDialog(this);
        JSONObject params = new JSONObject();
        try {
            params.put("userId", user_id);
            params.put("oldPassword", old_password);
            params.put("newPassword", new_password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.updatePassword, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("STATUS")) {
                        getSharedPreferences("userSession", 0).edit().clear().apply();
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(EditPassword.this, "ប្តូរពាក្យសម្ងាត់ជោគជ័យ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditPassword.this, "ប្តូរពាក្យសម្ងាត់បរាជ័យ", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditPassword.this, "There is Something Wrong !!", Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Edit Password");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.recreate();
    }
}
