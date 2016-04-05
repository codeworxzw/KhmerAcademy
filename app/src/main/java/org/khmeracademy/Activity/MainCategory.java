package org.khmeracademy.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.khmeracademy.Adapter.MainCatRecyclerAdapter;
import org.khmeracademy.CallBack.RecyclerItemClickListener;
import org.khmeracademy.Model.MainCategoryItem;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.R;
import org.khmeracademy.Util.ChangeLanguage;
import org.khmeracademy.Util.CustomDialog;
import org.khmeracademy.Util.MyNavigationDrawer;

import java.util.ArrayList;

/*public class MainCategory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {*/
public class MainCategory extends AppCompatActivity{
    private static final String TAG = MainCategory.class.getSimpleName();
    private Tracker mTracker;

    private ArrayList<MainCategoryItem> mCatList;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private SearchView searchView;
    private MenuItem menuItem;
    private MyNavigationDrawer nvd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new ChangeLanguage(this);

        setContentView(R.layout.activity_main);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));

        // Toggle the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Call navigation drawer
        nvd = new MyNavigationDrawer(this, R.id.nav_view);

        // Change from Navigation menu item image to arrow back image of toolbar
        toolbar.setNavigationIcon(R.drawable.menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

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

        //List main category from server
        listMainCattegoty();

        // Listen to the item touching
        mRecyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(
                        this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                Intent in = new Intent(MainCategory.this, SubCategory.class);
                                in.putExtra("mainId", mCatList.get(position).getCategoryId());
                                in.putExtra("title", mCatList.get(position).getTitle());
                                startActivity(in);
                            }
                        }));
    }
    //List all main category
    public void listMainCattegoty(){
        CustomDialog.showProgressDialog(this);
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, API.mainCategoryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("RES_DATA")) {
                        JSONArray jsonArray = response.getJSONArray("RES_DATA");
                        //list item
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MainCategoryItem item = new MainCategoryItem();
                            item.setCategoryId(jsonArray.getJSONObject(i).getString("mainCategoryId"));
                            item.setBgImage(jsonArray.getJSONObject(i).getString("backgroundImage"));
                            item.setColor(Color.parseColor("#4CAF50"));
                            if(!jsonArray.getJSONObject(i).getString("color").equals("null")){
                                item.setColor(Color.parseColor("#" + jsonArray.getJSONObject(i).getString("color")));
                            }
                            item.setTitle(jsonArray.getJSONObject(i).getString("mainCategoryName"));
                            item.setDescription(jsonArray.getJSONObject(i).getString("description"));
                            mCatList.add(item);
                        }
                    }else{
                        Toast.makeText(MainCategory.this, "No Maincategory Found !", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    CustomDialog.hideProgressDialog();
                    MainCatRecyclerAdapter adapter = new MainCatRecyclerAdapter(mCatList, getApplicationContext());
                    mRecyclerView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.hideProgressDialog();
                Toast.makeText(MainCategory.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    // Call to refress navigation header
    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            nvd.requestInfoNav();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Call DrawerLayout of navigation drawer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Menu inflater into layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_khmeracademy, menu);
        menuItem = menu.findItem(R.id.action_search);

        // Customize SearchView
        SearchView mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.searchSub));

        // SearchView is fully expanding in landscape mode
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        // Disable Fullscreen editing mode when enters SearchView
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

/*
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
*/

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                Intent in = new Intent(MainCategory.this, ActivitySearchSub.class);
                in.putExtra("textSearch", query);
                startActivity(in);
                try {
                    menuItem.collapseActionView(); //this will collapse your search view
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }

//                searchView.invokeClose();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Category");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.recreate();
    }

}