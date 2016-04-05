package org.khmeracademy.Activity;

import android.content.Intent;
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
import org.khmeracademy.Adapter.VideoListAdapter;
import org.khmeracademy.CallBack.RecyclerItemClickListener;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.CallBack.GetYoutubeDurationListener;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.NetworkRequest.Youtube;
import org.khmeracademy.R;
import org.khmeracademy.Util.CustomDialog;
import org.khmeracademy.Util.MyNavigationDrawer;
import org.khmeracademy.Model.ListVideoItem;

import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoList extends AppCompatActivity {
    private static final String TAG = VideoList.class.getSimpleName();
    private ArrayList<ListVideoItem> mVideoList;
    private RecyclerView mRecyclerView;
    private ImageView bgImageView;
    private View colorBackgroundView;
    private TextView catDescription, catTitle;
    private VideoListAdapter adapter;
    private Toolbar toolbar;
    private MyNavigationDrawer nvd;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        // Toolbar formation
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.html_title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));
        setSupportActionBar(toolbar);

        String subCatTitle = getIntent().getExtras().getString("catTitle");
        toolbar.setTitle(subCatTitle);

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

        // Call Navigation drawer
        nvd = new MyNavigationDrawer(this, R.id.nav_view);

        initViews();
        Picasso.with(this)
                .load(getIntent().getExtras().getString("bgImage"))
                .placeholder(R.drawable.thumbnail_not_yet_display)
                .error(R.drawable.thumbnail_not_yet_display).into(bgImageView);
        colorBackgroundView.setBackgroundColor(getIntent().getExtras().getInt("bgColor"));
        catTitle.setText(getIntent().getExtras().getString("catTitle"));
        catDescription.setText(getIntent().getExtras().getString("catDescription"));
        // numVideos.setBackgroundColor(getIntent().getExtras().getInt("bgColor"));

        // Setup layout manager for mBlogList and column count
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Control orientation of the mBlogList
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        // Attach layout manager
        mRecyclerView.setLayoutManager(layoutManager);

        // Bind adapter to recycler
        mVideoList = new ArrayList<>();

        /* API Access */
        final String playlistId = getIntent().getExtras().getString("catId");
        String url = API.listVideoByPlayListIdUrl + playlistId;
        CustomDialog.showProgressDialog(this);
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("RES_DATA")) {
                        JSONArray jsonArray = response.getJSONArray("RES_DATA");
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            final ListVideoItem listVideoItem = new ListVideoItem();
                            listVideoItem.setVideo_url(jsonObject.getString("youtubeUrl"));
                            Youtube.getYoutubeDuration(listVideoItem.getVideo_url(), new GetYoutubeDurationListener() {
                                @Override
                                public void onSuccess(String result) {
                                    listVideoItem.setDuration(result);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            listVideoItem.setVideoTitle(jsonObject.getString("videoName"));
                            listVideoItem.setListVideoNum(i + 1);
                            listVideoItem.setBgNumColor(getIntent().getExtras().getInt("bgColor"));
                            listVideoItem.setView_count(jsonObject.getInt("viewCounts"));
                            listVideoItem.setId(jsonObject.getString("videoId"));
                            listVideoItem.setVote(jsonObject.getInt("countVotePlus"));
                            mVideoList.add(listVideoItem);
                        }
                    } else {
                        Toast.makeText(VideoList.this, "មិនមានវីដេអូទេ", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VideoList.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
/* API Access */

        // Setting adapter
        adapter = new VideoListAdapter(mVideoList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());

        // Listen to the item touching
        mRecyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(
                        this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View itemView, int position) {
                                Intent in = new Intent(VideoList.this, VideoPlayer.class);
                                in.putExtra("playlist_id", playlistId);
                                in.putExtra("video_url", mVideoList.get(position).getVideo_url());
                                in.putExtra("video_title", mVideoList.get(position).getVideoTitle());
                                in.putExtra("view_count", mVideoList.get(position).getView_count());
                                in.putExtra("video_id", mVideoList.get(position).getId());
                                in.putExtra("position", position);
                                startActivity(in);
                            }
                        }));
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.video_list_recycler_view);
        catTitle = (TextView) findViewById(R.id.tv_title);
        bgImageView = (ImageView) findViewById(R.id.bgImageItem);
        colorBackgroundView = findViewById(R.id.colorItem);
        catDescription = (TextView) findViewById(R.id.describe);
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

    // Disable save menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_search1).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    // Menu inflater into layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_sub_category, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search1);

        // Customize SearchView
        SearchView mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setQueryHint("Type something...");

        // SearchView is fully expanding in landscape mode
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        // Disable Fullscreen editing mode when enters SearchView
        mSearchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Video List");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}