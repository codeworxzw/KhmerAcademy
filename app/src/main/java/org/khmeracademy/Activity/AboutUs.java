package org.khmeracademy.Activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.hyphen.SqueezeHyphenator;
import com.bluejamesbond.text.style.JustifiedSpan;
import com.bluejamesbond.text.style.TextAlignment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.khmeracademy.KhmerFont.ArticleBuilder;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.R;
import org.khmeracademy.Util.ChangeLanguage;
import org.khmeracademy.Util.MyNavigationDrawer;

public class AboutUs extends AppCompatActivity implements OnMapReadyCallback {

    private TextView textView;
    private Toolbar toolbar;
    private MyNavigationDrawer nvd;
    private Tracker mTracker;
    private TextView title;
    private TextView contact;
    private TextView location;
    private MapFragment mapFragment;


    protected int getContentView() {
        return R.layout.acitivity_about_us;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change default to Khmer Language
        new ChangeLanguage(this);

        setContentView(getContentView());
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.navItemAboutUs);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));

        // Change from Navigation menu item image to arrow back image of toolbar
        toolbar.setNavigationIcon(R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Who are we?
        title = (TextView) findViewById(R.id.txtWe);
        SpannableString content = new SpannableString(getText(R.string.about_hrd));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        content.setSpan(new ForegroundColorSpan(Color.BLUE), 0, content.length(), 0);
        title.setText(content);

        //Contact us
        contact = (TextView) findViewById(R.id.contactUs);
        SpannableString content1 = new SpannableString(getText(R.string.contactUs));
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        content1.setSpan(new ForegroundColorSpan(Color.BLUE), 0, content1.length(), 0);
        contact.setText(content1);

        //Location
        location = (TextView) findViewById(R.id.location);
        SpannableString content2 = new SpannableString(getText(R.string.location));
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        content2.setSpan(new ForegroundColorSpan(Color.BLUE), 0, content2.length(), 0);
        location.setText(content2);

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Call Navigation drawer
        nvd = new MyNavigationDrawer(this, R.id.nav_view);

        // Put Text to About us activity
        ArticleBuilder amb = new ArticleBuilder();
        amb.append(getText(R.string.aboutContext),
                true, new RelativeSizeSpan(0.8f), new JustifiedSpan(),
                new StyleSpan(Typeface.NORMAL), new ForegroundColorSpan(0xFF555555));

        addDocumentView(amb, DocumentView.FORMATTED_TEXT);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Call Event alert dialog for text view developerName ID
       /* textView = (TextView) findViewById(R.id.developerName);
        textView.setOnClickListener(this);*/
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

    public DocumentView addDocumentView(CharSequence article, int type, boolean rtl) {
        final DocumentView documentView = new DocumentView(this, type);
        documentView.getDocumentLayoutParams().setTextColor(0x00000000);
        /*documentView.getDocumentLayoutParams().setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/Battambang-Regular.ttf"));*/
        documentView.getDocumentLayoutParams().setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        documentView.getDocumentLayoutParams().setTextAlignment(TextAlignment.JUSTIFIED);
        documentView.getDocumentLayoutParams().setHyphenator(SqueezeHyphenator.getInstance());
        documentView.getDocumentLayoutParams().setLineHeightMultiplier(1f);
        documentView.setText(article);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.addView(documentView);

        LinearLayout articleList = (LinearLayout) findViewById(R.id.articleList);
        articleList.addView(linearLayout);
        return documentView;

    }

    public DocumentView addDocumentView(CharSequence article, int type) {
        return addDocumentView(article, type, false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_khmeracademy, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("About Us");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.recreate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // latitude and longitude
        double latitude = 11.575677;
        double longitude = 104.889179;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(getString(R.string.kshrd)).icon(BitmapDescriptorFactory.fromResource(R.drawable.hrd_logo));

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng kshrd = new LatLng(latitude, longitude);
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kshrd, 16));
    }
}











