package org.khmeracademy.Util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.khmeracademy.Activity.AboutUs;
import org.khmeracademy.Activity.EditProfile;
import org.khmeracademy.Activity.LanguageDialogFragment;
import org.khmeracademy.Activity.MainCategory;
import org.khmeracademy.Activity.RegisterActivity;
import org.khmeracademy.Activity.UserProfileDetail;
import org.khmeracademy.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Longdy on 1/19/2016.
 */
public class MyNavigationDrawer {
    private Activity mActivity;
    private TextView username;
    private TextView email;
    private CircleImageView imageProfile;
    private ImageView bgImage;
    private String className;
    private FragmentManager mFragmentManager;
    private LanguageDialogFragment mSettingDialogFragment;


    public MyNavigationDrawer(final Activity activity, int resId) {
        FacebookSdk.sdkInitialize(activity);
        mActivity = activity;
        // declare header item navigation view
        NavigationView navigationView = (NavigationView) mActivity.findViewById(resId);
        View header = navigationView.getHeaderView(0);
        username = (TextView) header.findViewById(R.id.userName);
        email = (TextView) header.findViewById(R.id.userEmail);
        imageProfile = (CircleImageView) header.findViewById(R.id.nv_profile_image);
        bgImage = (ImageView) header.findViewById(R.id.bgImageItem);
        className = activity.getClass().getName();

        try {
            requestInfoNav();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            intent.putExtra("userId", "" + mActivity.getSharedPreferences("userSession", 0).getString("id", "N/A"));
                            mActivity.startActivity(intent);
                        }
                        break;

                    case R.id.nav_profile:

                        if (className.equals("UserProfileDetail")) {
                            Toast.makeText(mActivity, "Now, you are in profile detail !", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(mActivity, UserProfileDetail.class);
                            intent.putExtra("userId", "" + mActivity.getSharedPreferences("userSession", 0).getString("id", "N/A"));
                            mActivity.startActivity(intent);
                        }
                        break;

                    // Handle logout action
                    case R.id.nav_logout:
                        Session.clearSession();
                        // Logout Facebook Account
                        if (LoginManager.getInstance() != null){
                            LoginManager.getInstance().logOut();
                        }
                        intent = new Intent(mActivity, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                        break;

                    // Handle language action
                    case R.id.nav_language:

                        mFragmentManager = mActivity.getFragmentManager();
                        mSettingDialogFragment = new LanguageDialogFragment();
                        mSettingDialogFragment.show(mFragmentManager, "setting_dialog_fragment");
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void requestInfoNav() throws JSONException {
        String user_name = Session.userName;
        String user_email = Session.email;
        String profile_image_url = Session.profile_picture;

        username.setText(user_name);
        email.setText(user_email);

        Picasso.with(mActivity)
                .load(profile_image_url)
                .placeholder(R.drawable.icon_user)
                .error(R.drawable.icon_user).into(imageProfile);

        Picasso.with(mActivity)
                .load(profile_image_url)
                .placeholder(R.drawable.icon_user)
                .error(R.drawable.icon_user).into(bgImage);
    }
}
