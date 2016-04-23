package org.khmeracademy.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import org.khmeracademy.Util.CustomDialog;
import org.khmeracademy.Util.MyNavigationDrawer;
import org.khmeracademy.Model.SubCategoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivitySearchSub extends AppCompatActivity{
    private static final String TAG = SubCategory.class.getSimpleName();
    private Tracker mTracker;
    private ArrayList<SubCategoryItem> mCatList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private String textSearch;

    // Setting adapter
    private SubCatRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.khmeracademy.R.layout.activity_activity_search_sub);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        textSearch = getIntent().getExtras().getString("textSearch");

        // Call Toolbar
        // Toolbar formation
        toolbar = (Toolbar) findViewById(org.khmeracademy.R.id.toolbar);
        toolbar.setTitle("Search Lesson");
        toolbar.setTitleTextColor(getResources().getColor(org.khmeracademy.R.color.textColor));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(org.khmeracademy.R.drawable.menu);
        getSupportActionBar().setHomeAsUpIndicator(org.khmeracademy.R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // back to previous activity
            }
        });

        MyNavigationDrawer nvd = new MyNavigationDrawer(this, org.khmeracademy.R.id.nav_view);

        initViews();

        // Setup layout manager for mBlogList and column count
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Control orientation of the mBlogList
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        // Attach layout manager
        mRecyclerView.setLayoutManager(layoutManager);

        listAllSubCategory();
        // Listen to the item touching
        mRecyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(
                        this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                SubCategoryItem sc = mCatList.get(position);
                                Intent in = new Intent(ActivitySearchSub.this, VideoList.class);
                                in.putExtra("catId", sc.getSubCategoryID());
                                in.putExtra("catTitle", sc.getTitle());
                                in.putExtra("catDescription", sc.getDescribe());
                                in.putExtra("bgImage", sc.getBgImage());
                                in.putExtra("bgColor", sc.getColor());
                                startActivity(in);

                            }
                        }));
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(org.khmeracademy.R.id.sub_cat_recycler_view);
    }

    // Disable save menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(org.khmeracademy.R.id.action_save).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    // Menu inflater into layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(org.khmeracademy.R.menu.activity_sub_category, menu);
        MenuItem menuItem = menu.findItem(org.khmeracademy.R.id.action_search1);
        menuItem.expandActionView();

        // Customize SearchView
        SearchView mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setQueryHint("ស្វែងរក Playlist...");
        mSearchView.setQuery(textSearch, false);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // SearchView is fully expanding in landscape mode
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(org.khmeracademy.R.id.action_search1).getActionView();


        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }


        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered

                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                //Toast.makeText(ActivitySearchSub.this, query , Toast.LENGTH_SHORT).show();
                textSearch = query;
                adapter.clearData();
                listAllSubCategory();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }
    //List all searched sub-category
    public void listAllSubCategory(){

        String url = API.searchSubcategory + textSearch;
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
                                item.setColor(Color.parseColor("#" + jsonArray.getJSONObject(i).getString("color")));
                            }
                            item.setSubCategoryID(jsonArray.getJSONObject(i).getString("playlistId"));
                            item.setTitle(jsonArray.getJSONObject(i).getString("playlistName"));
                            item.setNumVideos(10 + i);
                            item.setDescribe(jsonArray.getJSONObject(i).getString("description"));
                            item.setNumVideos(jsonArray.getJSONObject(i).getInt("countVideos"));
                            mCatList.add(item);
                        }
                    }else{
                        Toast.makeText(ActivitySearchSub.this, "No Playlists !!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    CustomDialog.hideProgressDialog();
                    // Setting adapter
                    adapter = new SubCatRecyclerAdapter(mCatList, getApplicationContext()) ;
                    mRecyclerView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.hideProgressDialog();
                Toast.makeText(ActivitySearchSub.this, "OOP, Something Wrong !! Check your connection...", Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Search Playlist");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
