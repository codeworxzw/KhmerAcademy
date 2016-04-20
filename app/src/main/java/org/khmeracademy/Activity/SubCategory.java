package org.khmeracademy.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import org.khmeracademy.Adapter.SubCatRecyclerAdapter;
import org.khmeracademy.CallBack.RecyclerItemClickListener;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.R;
import org.khmeracademy.Util.CustomDialog;
import org.khmeracademy.Util.MyNavigationDrawer;
import org.khmeracademy.Model.SubCategoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*public class SubCategory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {*/
public class SubCategory extends AppCompatActivity {
    private static final String TAG = SubCategory.class.getSimpleName();
    private Tracker mTracker;

    private ArrayList<SubCategoryItem> mCatList;

    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private MenuItem menuItem;
    private SearchView mSearchView, searchView;
    private MyNavigationDrawer nvd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        // Toolbar formation
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getExtras().getString("title"));
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // back to previous activity
            }
        });


        nvd = new MyNavigationDrawer(this, R.id.nav_view);

        initViews();

        // Setup layout manager for mBlogList and column count
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Control orientation of the mBlogList
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        // Attach layout manager
        mRecyclerView.setLayoutManager(layoutManager);

        // Bind adapter to recycler
        mCatList = new ArrayList<>();

        listSubCategory();

        // Listen to the item touching
        mRecyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(
                        this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                SubCategoryItem sc = mCatList.get(position);
                                Intent in = new Intent(SubCategory.this, VideoList.class);
                                in.putExtra("catId", mCatList.get(position).getSubCategoryID());
                                in.putExtra("catTitle", sc.getTitle());
                                in.putExtra("bgImage", mCatList.get(position).getBgImage());
                                in.putExtra("bgColor", mCatList.get(position).getColor());
                                in.putExtra("catDescription", sc.getDescribe());
                                startActivity(in);
                            }
                        }));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            nvd.requestInfoNav();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //List sub-category by ID
    public void listSubCategory(){

        String url = API.subCategoryById + getIntent().getExtras().getString("mainId");
        CustomDialog.showProgressDialog(this);
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("RES_DATA")) {
                        JSONArray jsonArray = response.getJSONArray("RES_DATA");

                        //list item
                        for (int i = 0; i < jsonArray.length(); i++) {
                            SubCategoryItem item = new SubCategoryItem();
                            item.setBgImage(jsonArray.getJSONObject(i).getString("bgImage"));
                            item.setColor(Color.parseColor("#4CAF50"));
                            if(!jsonArray.getJSONObject(i).getString("color").equals("null")){
                                try {
                                    item.setColor(Color.parseColor("#" + jsonArray.getJSONObject(i).getString("color")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    item.setColor(Color.parseColor("#4CAF50"));
                                }
                            }
                            item.setSubCategoryID(jsonArray.getJSONObject(i).getString("playlistId"));
                            item.setTitle(jsonArray.getJSONObject(i).getString("playlistName"));
                            item.setNumVideos(10 + i);
                            item.setDescribe(jsonArray.getJSONObject(i).getString("description"));
                            item.setNumVideos(jsonArray.getJSONObject(i).getInt("countVideos"));
                            mCatList.add(item);
                        }
                    }else{
                        Toast.makeText(SubCategory.this, "មិនមាន Playlist ទេ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    CustomDialog.hideProgressDialog();
                    // Setting adapter
                    final SubCatRecyclerAdapter adapter = new SubCatRecyclerAdapter(mCatList, getApplicationContext()) ;
                    mRecyclerView.setAdapter(adapter);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.hideProgressDialog();
                Toast.makeText(SubCategory.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.sub_cat_recycler_view);
    }

    // Disable save menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menu.findItem(R.id.action_save).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    // Menu inflater into layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_khmeracademy, menu);
        menuItem = menu.findItem(R.id.action_search);

        // Customize SearchView
        mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setQueryHint("ស្វែងរក Playlist...");

        // SearchView is fully expanding in landscape mode
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        // Disable Fullscreen editing mode when enters SearchView
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Intent in = new Intent(SubCategory.this, ActivitySearchSub.class);
                in.putExtra("textSearch", query);
                startActivity(in);
                try {
                    menuItem.collapseActionView(); //this will collapse your search view
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    System.out.println(ex);
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Playlist");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.recreate();
    }
}
