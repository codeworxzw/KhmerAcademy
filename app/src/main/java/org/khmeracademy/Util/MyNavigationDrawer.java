package org.khmeracademy.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.khmeracademy.Activity.AboutUs;
import org.khmeracademy.Activity.EditProfile;
import org.khmeracademy.Activity.MainCategory;
import org.khmeracademy.Activity.RegisterActivity;
import org.khmeracademy.Activity.UserProfileDetail;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Longdy on 1/19/2016.
 */
public class MyNavigationDrawer {
    private String userId;
    private Activity mActivity;
    private TextView username;
    private TextView email;
    private CircleImageView imageProfile;
    private String className;

    public MyNavigationDrawer(final Activity activity, int resId) {
        mActivity = activity;
        // declare header item navigation view
        NavigationView navigationView = (NavigationView) mActivity.findViewById(resId);
        View header = navigationView.getHeaderView(0);
        username = (TextView) header.findViewById(org.khmeracademy.R.id.userName);
        email = (TextView) header.findViewById(org.khmeracademy.R.id.userEmail);
        imageProfile = (CircleImageView) header.findViewById(org.khmeracademy.R.id.nv_profile_image);
        className = activity.getClass().getName();

        try {
            requestInfoNav();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userId = mActivity.getSharedPreferences("userSession", 0).getString("id", "N/A");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                Intent intent;
                className = className.substring(className.lastIndexOf(".") + 1);
                switch (item.getItemId()) {
                    // Handle home action
                    case R.id.nav_home:
                        if (className.equals("MainCategory")) {
                            Toast.makeText(mActivity, "Now, you are in homepage !", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(mActivity, MainCategory.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }
                        break;

                    // Handle aboutus action
                    case R.id.nav_about:

                        if (className.equals("AboutUs")) {
                            Toast.makeText(mActivity, "Now, you are in about us !", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(mActivity, AboutUs.class);
                            mActivity.startActivity(intent);
                        }
                        break;

                    // Handle setting action
                    case R.id.nav_setting:
                        if (className.equals("EditProfile")) {
                            Toast.makeText(mActivity, "Now, you are in edit profile!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(mActivity, EditProfile.class);
                            intent.putExtra("userId", "" + userId);
                            mActivity.startActivity(intent);
                        }
                        break;

                    case R.id.nav_profile:

                        if (className.equals("UserProfileDetail")) {
                            Toast.makeText(mActivity, "Now, you are in profile detail !", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(mActivity, UserProfileDetail.class);
                            intent.putExtra("userId", "" + userId);
                            mActivity.startActivity(intent);
                        }
                        break;

                    // Handle logout action
                    case R.id.nav_logout:
                        mActivity.getSharedPreferences("userSession", 0).edit().clear().apply();
                        intent = new Intent(mActivity, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(org.khmeracademy.R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void requestInfoNav() throws JSONException {
        SharedPreferences session = mActivity.getBaseContext().getSharedPreferences("userSession", 0);
        String user_name = session.getString("userName", "N/A");
        String user_email = session.getString("email", "N/A");
        String profile_image_url = session.getString("profile_picture", "N/A");

        username.setText(user_name);
        email.setText(user_email);
        Picasso.with(mActivity)
                .load(API.BASE_URL + "/resources/upload/file/" + profile_image_url)
                .placeholder(org.khmeracademy.R.drawable.icon_user)
                .error(org.khmeracademy.R.drawable.icon_user).into(imageProfile);
    }
}
