package org.khmeracademy.Activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubePlayer;
import org.khmeracademy.Adapter.PlaylistRecyclerAdapter;
import org.khmeracademy.CallBack.DialogCallBack;
import org.khmeracademy.CallBack.RecyclerItemClickListener;
import org.khmeracademy.Fragment.MyDialogFragment;
import org.khmeracademy.CallBack.YoutubePlayBackListener;
import org.khmeracademy.Fragment.YouTubeFragment;
import org.khmeracademy.Model.PlayListItem;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.CallBack.GetYoutubeDurationListener;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.NetworkRequest.Youtube;
import org.khmeracademy.R;
import org.khmeracademy.Util.MyNavigationDrawer;
import org.khmeracademy.Util.SmoothLinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoPlayer extends AppCompatActivity implements YoutubePlayBackListener, DialogCallBack {
    // private Facebook facebook = new Facebook(APP_ID);
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    private Toolbar toolbar;
    private ArrayList<PlayListItem> mPlayListItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mDecriptionTitle, mView, numberOfLike;
    private ImageView like;
    private YouTubeFragment youTubeFragment;
    private SmoothLinearLayoutManager layoutManager = new SmoothLinearLayoutManager(this);
    private PlaylistRecyclerAdapter adapter;
    private String video_id;
    private MyNavigationDrawer nvd;
    private boolean isLike;
    private int current_position;
    private LinearLayout like_layout, comment_layout, share_layout;
    private YouTubePlayer youTubePlayer;
    private Tracker mTracker;
    private MyDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Get Intent Extra Data
        current_position = getIntent().getExtras().getInt("position", 0);
        video_id = getIntent().getExtras().getString("video_id", "N/A");

        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        FacebookSdk.sdkInitialize(VideoPlayer.this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(VideoPlayer.this);

        // Call Toolbar
        // Toolbar formation
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getExtras().getString("video_title"));
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Description & View Label
        initView();

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // back to previous activity
            }
        });

        mRecyclerView.setHasFixedSize(true);
        // Attach layout manager
        mRecyclerView.setLayoutManager(layoutManager);
        // Setting adapter
        adapter = new PlaylistRecyclerAdapter(mPlayListItems, VideoPlayer.this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            adapter.setSelected(position);
                            mRecyclerView.smoothScrollToPosition(position);
                            mDecriptionTitle.setText(mPlayListItems.get(position).getTitle());
                            mView.setText(mPlayListItems.get(position).getView_count() + " views");
                            toolbar.setTitle(mPlayListItems.get(position).getTitle());
                            // show number of Like of current position
                            numberOfLike.setText(mPlayListItems.get(position).getVote() + "");
                            // for calculate number of like on updateLike
                            current_position = position;
                            video_id = mPlayListItems.get(position).getId();
                            checkLikeForVideo(getSharedPreferences("userSession", 0).getString("id", null), video_id);
                            // Replace old vdo with new vdo
                            youTubePlayer.loadVideo(mPlayListItems.get(position).getVideo_url());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
        );

/* API Access */
        comment_layout.setEnabled(false);
        like_layout.setEnabled(false);
        share_layout.setEnabled(false);
        final String playlistId = getIntent().getExtras().getString("playlist_id");
        String url = API.listVideoByPlayListIdUrl + playlistId;
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("RES_DATA")) {
                        JSONArray jsonArray = response.getJSONArray("RES_DATA");
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            final PlayListItem playListItem = new PlayListItem();
                            playListItem.setVideo_url(jsonObject.getString("youtubeUrl"));
                            Youtube.getYoutubeDuration(playListItem.getVideo_url(), new GetYoutubeDurationListener() {
                                @Override
                                public void onSuccess(String result) {
                                    playListItem.setDuration(result);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            playListItem.setTitle(jsonObject.getString("videoName"));
                            playListItem.setId(jsonObject.getString("videoId"));
                            playListItem.setDescription(jsonObject.getString("description"));
                            playListItem.setView_count(jsonObject.getInt("viewCounts"));
                            playListItem.setVote(jsonObject.getInt("countVotePlus"));
                            mPlayListItems.add(playListItem);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    comment_layout.setEnabled(true);
                    like_layout.setEnabled(true);
                    share_layout.setEnabled(true);
                    numberOfLike.setText(mPlayListItems.get(current_position).getVote() + "");
                    adapter.setSelected(current_position);
                    mRecyclerView.smoothScrollToPosition(current_position);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoPlayer.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
/* API Access */

        mDecriptionTitle.setText(getIntent().getExtras().getString("video_title"));
        mView.setText(getIntent().getExtras().getInt("view_count") + " views");

        // insert vdo player fragment
        youTubeFragment = YouTubeFragment.newInstance(getIntent().getExtras().getString("video_url"));
        getSupportFragmentManager().beginTransaction().add(R.id.youtube_fragment, youTubeFragment).commit();

        // Click on Comment Layout
        comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment = MyDialogFragment.newInstance(video_id);
                dialogFragment.show(getSupportFragmentManager(), "comment");
                if (youTubePlayer != null) {
                    if (youTubePlayer.isPlaying()){
                        youTubePlayer.pause();
                    }
                }
            }
        });

        // Click on Like Layout
        like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLike(getSharedPreferences("userSession", 0).getString("id", null), video_id);
            }
        });

        checkLikeForVideo(getSharedPreferences("userSession", 0).getString("id", null), video_id);
        //tvShare.setShareContent(content);

        //Click on share text
        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(mPlayListItems.get(current_position).getTitle())
                            .setContentDescription(mPlayListItems.get(current_position).getDescription())
                            .setContentUrl(Uri.parse("https://www.youtube.com/watch?v=" + mPlayListItems.get(current_position).getVideo_url()))
                            .setImageUrl(Uri.parse("http://img.youtube.com/vi/" + mPlayListItems.get(current_position).getVideo_url() + "/mqdefault.jpg"))
                            .build();
                    shareDialog.show(linkContent);
                }

                /*String url = "https://www.youtube.com/watch?v=" + mPlayListItems.get(current_position).getVideo_url();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);

// See if official Facebook app is found
                boolean facebookAppFound = false;
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                        intent.setPackage(info.activityInfo.packageName);
                        facebookAppFound = true;
                        break;
                    }
                }
                // As fallback, launch sharer.php in a browser
                if (!facebookAppFound) {
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                }

                startActivity(intent);*/

            }
        });
    }

    /* Update Like */
    private void updateLike(final String user_id, final String video_id) {
        like_layout.setEnabled(false);
        final String url = API.checkLikeVideo + "/u/" + user_id + "/v/" + video_id;
        if (isLike) {
            GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.PUT, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("STATUS")) {
                            mPlayListItems.get(current_position).setVote(mPlayListItems.get(current_position).getVote() - 1);
                            numberOfLike.setText(mPlayListItems.get(current_position).getVote() + "");
                            isLike = !isLike;
                            Toast.makeText(VideoPlayer.this, R.string.unlike, Toast.LENGTH_SHORT).show();
                            like.setImageResource(R.drawable.ic_unlike);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        like_layout.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    like_layout.setEnabled(true);
                    Toast.makeText(VideoPlayer.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
                }
            });
            VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
        } else {
            GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("STATUS")) {
                            mPlayListItems.get(current_position).setVote(mPlayListItems.get(current_position).getVote() + 1);
                            numberOfLike.setText(mPlayListItems.get(current_position).getVote() + "");
                            isLike = !isLike;
                            Toast.makeText(VideoPlayer.this, R.string.unlike, Toast.LENGTH_SHORT).show();
                            like.setImageResource(R.drawable.ic_like);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        like_layout.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    like_layout.setEnabled(true);
                    Toast.makeText(VideoPlayer.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
                }
            });
            VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
        }
    }

    /* Check if user already like video or not yet ! */
    private void checkLikeForVideo(final String user_id, final String video_id) {
        final String url = API.checkLikeVideo + "/u/" + user_id + "/v/" + video_id;
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    isLike = response.getBoolean("CHECKVOTE");
                    if (isLike) {
                        like.setImageResource(R.drawable.ic_like);
                    } else {
                        like.setImageResource(R.drawable.ic_unlike);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VideoPlayer.this, R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    @Override
    public void onPlayerInitialize(YouTubePlayer youTubePlayer) {
        this.youTubePlayer = youTubePlayer;
    }

    @Override
    public void onFullScreen() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onPlayNextVideo() {
        try {
            if (current_position != (mPlayListItems.size() - 1)) {
                current_position++;
                adapter.setSelected(current_position);
                mRecyclerView.smoothScrollToPosition(current_position);
                mDecriptionTitle.setText(mPlayListItems.get(current_position).getTitle());
                toolbar.setTitle(mPlayListItems.get(current_position).getTitle());
                mView.setText(mPlayListItems.get(current_position).getView_count() + " views");
                // show number of Like of current position
                numberOfLike.setText(mPlayListItems.get(current_position).getVote() + "");
                // for calculate number of like on updateLike
                video_id = mPlayListItems.get(current_position).getId();
                checkLikeForVideo(getSharedPreferences("userSession", 0).getString("id", null), video_id);
                // Replace old vdo with new vdo
                youTubePlayer.loadVideo(mPlayListItems.get(current_position).getVideo_url());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        like = (ImageView) findViewById(R.id.iv_like);
        numberOfLike = (TextView) findViewById(R.id.txt_like);
        mDecriptionTitle = (TextView) findViewById(R.id.tv_playlist_description_title);
        mView = (TextView) findViewById(R.id.tv_playlist_description_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.playlist_recycler_view);
        comment_layout = (LinearLayout) findViewById(R.id.layout_comment);
        like_layout = (LinearLayout) findViewById(R.id.layout_like);
        share_layout = (LinearLayout) findViewById(R.id.layout_share);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        mTracker.setScreenName("Video Player");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDismissDialog() {
        if (youTubePlayer != null) {
            if (!youTubePlayer.isPlaying()){
                youTubePlayer.play();
            }
        }
    }
}
