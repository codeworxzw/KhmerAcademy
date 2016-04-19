package org.khmeracademy.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.khmeracademy.Adapter.DepartmentSpinnerAdapter;
import org.khmeracademy.Adapter.UniversitySpinnerAdapter;
import org.khmeracademy.Model.DepartmentItem;
import org.khmeracademy.Model.UniversityItem;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.R;
import org.khmeracademy.Util.BitmapEfficient;
import org.khmeracademy.Util.ChangeLanguage;
import org.khmeracademy.Util.CustomDialog;
import org.khmeracademy.Util.MultipartUtility;
import org.khmeracademy.Util.MyNavigationDrawer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Manith on 12/28/2015.
 */
public class EditProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textPassword;
    private EditText etUsername, etDOB;
    private DatePicker datePicker;
    private int year, month, day;
    private Calendar calendar;
    private MyNavigationDrawer nvd;
    private CircleImageView imageProfile;
    private Bitmap bitmap;
    private String imgUrl;
    private String imagePath;
    private String[] uploadImgPath;
    private String picturePath = null;
    private static int RESULT_LOAD_IMAGE_PROFILE = 1;
    private EditText username;
    private EditText dateOfBirth;
    private EditText phone;
    private TextView userImageUrl;
    private boolean isChangeProfileImage;
    String user_id;
    Tracker mTracker;
    ArrayList<UniversityItem> mUniversityList = new ArrayList<>();
    ArrayList<DepartmentItem> mDepartmentList = new ArrayList<>();
    UniversitySpinnerAdapter universitySpinnerAdapter;
    DepartmentSpinnerAdapter departmentSpinnerAdapter;
    Spinner selectUniversity;
    Spinner selectDepartment;
    String universityName, universityId;
    String departmentName, departmentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ChangeLanguage(this);

        setContentView(R.layout.activity_edit_profile);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        user_id = getIntent().getStringExtra("userId");

        // Call Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.edit_profile);
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


        // Call Navigation drawer
        nvd = new MyNavigationDrawer(this, R.id.nav_view);

        //Go to EditPassword activity
        textPassword = (TextView) findViewById(R.id.logToEditPassword);
        textPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPwd = new Intent(getApplicationContext(), EditPassword.class);
                startActivity(intentPwd);
            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        etDOB = (EditText) findViewById(R.id.editDateOfBirth);
        etDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    setDate(v);
                } else {
                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });


        imageProfile = (CircleImageView) findViewById(R.id.editProfileImage);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE_PROFILE);
                //Toast.makeText(EditProfile.this, "Select Image choosing", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            requestResponse(user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Initialize layout edit-profile objects
        username = (EditText) findViewById(R.id.editUsername);

        dateOfBirth = (EditText) findViewById(R.id.editDateOfBirth);
        //department = (EditText) findViewById(R.id.editDepartment);
//        university = (EditText) findViewById(R.id.editSchool);
        phone = (EditText) findViewById(R.id.editPhone);
        userImageUrl = (TextView) findViewById(R.id.user_image_url);
        selectUniversity = (Spinner) findViewById(R.id.editSchool);
        selectDepartment = (Spinner) findViewById(R.id.editDepartment);
//        selectUniversity.setBackgroundColor(Color.parseColor("#FF0000"));

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getApplicationContext(),R.array.school_array, android.R.layout.simple_spinner_item);
//        ArrayAdapter<UniversityItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mUniversityList);


        universitySpinnerAdapter = new UniversitySpinnerAdapter(this, R.layout.spiner_item, R.id.university_name, mUniversityList);
        departmentSpinnerAdapter = new DepartmentSpinnerAdapter(this, R.layout.spiner_item, R.id.university_name, mDepartmentList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectUniversity.setAdapter(universitySpinnerAdapter);
        selectDepartment.setAdapter(departmentSpinnerAdapter);

        selectUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                universityId = mUniversityList.get(position).getuId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departmentId = mDepartmentList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listUniversity_Department();



    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        etDOB.setText("" + new StringBuilder().append(year).append("-").append(month).append("-").append(day), TextView.BufferType.EDITABLE);
    }

    // Get selected image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE_PROFILE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            if (imageHeight > 400 && imageWidth > 400) {
                bitmap = BitmapEfficient.decodeSampledBitmapFromFile(picturePath, 400, 400);
            } else {
                bitmap = BitmapFactory.decodeFile(picturePath);
            }
            imageProfile.setImageBitmap(bitmap);
            isChangeProfileImage = true;
        }
    }

    // upload image process background
    private class UploadTask extends AsyncTask<String, Void, Void> {
        String url = "http://api.khmeracademy.org/api/uploadfile/upload?url=user";
        String charset = "UTF-8";
        String responseContent = null;
        File file = null;

        @Override
        protected Void doInBackground(String... params) {
            sendFileToServer(params[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            file = BitmapEfficient.persistImage(bitmap, getApplicationContext());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //  progressSpinning.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
            if (responseContent != null) {
                try {
                    JSONObject object = new JSONObject(responseContent);
                    if (object.getBoolean("STATUS") == true) {
                        imgUrl = object.getString("IMG");
                        uploadImgPath = imgUrl.split("file/");
                        imagePath = uploadImgPath[1];
                        Toast.makeText(EditProfile.this, "Change Successfully !", Toast.LENGTH_SHORT).show();
                        requestUpdate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(EditProfile.this, "Uploaded Failed!", Toast.LENGTH_SHORT).show();
            }
        }

        // upload large file size
        public void sendFileToServer(String filePath) {
            try {
                MultipartUtility multipart = new MultipartUtility(url, charset);
                multipart.addFilePart("fileUpload", file);
                List<String> response = multipart.finish();
                for (String line : response) {
                    if (line != null) {
                        responseContent = line;
                        break;
                    }
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }

        }


    }

    // update user name
    public void requestUpdate() {
        SharedPreferences session = getBaseContext().getSharedPreferences("userSession", 0);
        imagePath = (isChangeProfileImage) ? imagePath : session.getString("profile_picture", "N/A");
        JSONObject params;
        try {
            params = new JSONObject();
            params.put("username", username.getText().toString().trim());
            params.put("dateOfBirth", dateOfBirth.getText().toString());
            params.put("userImageUrl", imagePath);
            params.put("gender", session.getString("gender", "N/A"));
            params.put("departmentId", departmentId);
            params.put("universityId", universityId);
            params.put("phoneNumber", phone.getText().toString().trim());
            params.put("userId", user_id);

            GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.PUT, API.editProfile, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("STATUS")) {
                            try {
                                getBaseContext().getSharedPreferences("userSession", 0).edit()
                                        .putString("profile_picture", imagePath)
                                        .putString("userName", username.getText().toString().trim())
                                        .apply();
                                nvd.requestInfoNav();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getBaseContext(), "Successfully Edited", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(EditProfile.this, "Unsuccessfully Edited !!", Toast.LENGTH_LONG).show();
                    } finally {
                        CustomDialog.hideProgressDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    CustomDialog.hideProgressDialog();
                    Toast.makeText(getBaseContext(), "ERROR_MESSAGE_NO_REPONSE: " + volleyError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
        } catch (JSONException e) {
            Toast.makeText(EditProfile.this, "ERROR_MESSAGE_JSONOBJECT" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(EditProfile.this, "ERROR_MESSAGE_EXP" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Load image from server
    public void requestResponse(String user_id) throws JSONException {
        String url = API.profileDetail + "/" + user_id;

        CustomDialog.showProgressDialog(this);
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("STATUS")) {
                        JSONObject object = response.getJSONObject("RES_DATA");
                        username.setText(object.getString("username"));
                        dateOfBirth.setText(object.getString("dateOfBirth"));
                        universityName = object.getString("universityName");
                        departmentName = object.getString("departmentName");
                        phone.setText(object.getString("phoneNumber").equals("null") ? "N/A" : object.getString("phoneNumber"));
                        imagePath = object.getString("userImageUrl");
                        final String imgProfile = API.BASE_URL + "/resources/upload/file/" + imagePath;
                        Picasso.with(getApplicationContext())
                                .load(imgProfile)
                                .placeholder(R.drawable.icon_user)
                                .error(R.drawable.icon_user).into(imageProfile);
                    } else {
                        Toast.makeText(EditProfile.this, "Invalid User Id", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditProfile.this, "There is Something Wrong !!", Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    // Load University
    public void listUniversity_Department() {
        //CustomDialog.showProgressDialog(this);
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, API.listUniversity_Department, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("UNIVERSITY")) {
                        JSONArray jsonArray = response.getJSONArray("UNIVERSITY");
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            UniversityItem universityItem = new UniversityItem(jsonObject.getString("universityId"), jsonObject.getString("universityName"));
                            mUniversityList.add(universityItem);
                            universitySpinnerAdapter.notifyDataSetChanged();
                            if (mUniversityList.get(i).getuName().equals(universityName)){
                                selectUniversity.setSelection(universitySpinnerAdapter.getPosition(mUniversityList.get(i)));
                            }
                        }
                    }

                    if (response.has("DEPARTMENT")) {
                        JSONArray jsonArray = response.getJSONArray("DEPARTMENT");
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            DepartmentItem departmentItem = new DepartmentItem(jsonObject.getString("departmentId"), jsonObject.getString("departmentName"));
                            mDepartmentList.add(departmentItem);
                            departmentSpinnerAdapter.notifyDataSetChanged();
                            if (mDepartmentList.get(i).getName().equals(departmentName)){
                                selectDepartment.setSelection(departmentSpinnerAdapter.getPosition(mDepartmentList.get(i)));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    //CustomDialog.hideProgressDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //CustomDialog.hideProgressDialog();
                Toast.makeText(EditProfile.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    // Disable search menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(R.id.action_search1).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sub_category, menu);
        return true;
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

    // Save item event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                CustomDialog.showProgressDialog(this);
                if (isChangeProfileImage) {
                    new UploadTask().execute(picturePath);
                } else {
                    requestUpdate();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Edit Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.recreate();
    }

}
