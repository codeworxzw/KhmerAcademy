package org.khmeracademy.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This Class uses to keep user info when user login.
 */
public class Session {

    /* Key */
    private static final String ID = "id";
    private static final String NAME = "userName";
    private static final String GENDER = "gender";
    private static final String EMAIL = "email";
    private static final String PROFILE_PICTURE = "profile_picture";
    private static final String COVER_PICTURE = "cover_picture";
    private static final String IS_LOGIN = "isLogIn";

    /* Field */
    public static String id;
    public static String userName;
    public static String email;
    public static String profile_picture;
    public static String cover_picture;
    public static String gender;
    public static boolean isLogin;

    /* Preference */
    private static String sharedPrefName = "userSession";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor sharedPrefEditor;
    private static Context mContext;

    public static void setContext(Context ctx) {
        mContext = ctx;
    }

    // Save User info to SharedPreferenceFile
    public static void saveUserSession(Context context, String userId, String name, String userEmail, String userGender, String profileUrl, String coverUrl) {
        mContext = context;
        id = userId;
        userName = name;
        email = userEmail;
        gender = userGender;
        profile_picture = profileUrl;
        cover_picture = coverUrl;
        isLogin = true;

        sharedPref = mContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString(ID, userId);
        sharedPrefEditor.putString(GENDER, userGender);
        sharedPrefEditor.putString(NAME, name);
        sharedPrefEditor.putString(EMAIL, userEmail);
        sharedPrefEditor.putString(PROFILE_PICTURE, profileUrl);
        sharedPrefEditor.putString(COVER_PICTURE, coverUrl);
        sharedPrefEditor.putBoolean(IS_LOGIN, true);
        sharedPrefEditor.apply();
    }

    // Read User info to SharedPreferenceFile
    public static void readUserSession(Context context) {
        mContext = context;
        sharedPref = mContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        id = sharedPref.getString(ID, null);
        userName = sharedPref.getString(NAME, null);
        gender = sharedPref.getString(GENDER, null);
        email = sharedPref.getString(EMAIL, null);
        profile_picture = sharedPref.getString(PROFILE_PICTURE, null);
        cover_picture = sharedPref.getString(COVER_PICTURE, null);
        isLogin = sharedPref.getBoolean(IS_LOGIN, false);
    }

    // Clear Session or clean all data from SharedPreferenceFile
    public static void clearSession() {
        mContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static void updateName(String newName) {
        userName = newName;
        sharedPref = mContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString(NAME, newName);
        sharedPrefEditor.apply();
    }

    public static void updateGender(String newGender) {
        gender = newGender;
        sharedPref = mContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString(GENDER, newGender);
        sharedPrefEditor.apply();
    }

    public static void updateProfilePicture(String imgUrl) {
        profile_picture = imgUrl;
        sharedPref = mContext.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString(PROFILE_PICTURE, imgUrl);
        sharedPrefEditor.apply();
    }

}
