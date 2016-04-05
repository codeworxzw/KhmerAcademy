package org.khmeracademy.Activity;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.hyphen.SqueezeHyphenator;
import com.bluejamesbond.text.style.JustifiedSpan;
import com.bluejamesbond.text.style.TextAlignment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import org.khmeracademy.KhmerFont.ArticleBuilder;
import org.khmeracademy.NetworkRequest.MyApplication;
import org.khmeracademy.Util.MyNavigationDrawer;

import org.json.JSONException;

public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private Toolbar toolbar;
    private MyNavigationDrawer nvd;
    private Tracker mTracker;

    protected int getContentView() {
        return org.khmeracademy.R.layout.acitivity_about_us;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        // Call Toolbar
        toolbar = (Toolbar) findViewById(org.khmeracademy.R.id.toolbar_main);
        toolbar.setTitle(org.khmeracademy.R.string.navItemAboutUs);
        toolbar.setTitleTextColor(getResources().getColor(org.khmeracademy.R.color.textColor));

        // Change from Navigation menu item image to arrow back image of toolbar
        toolbar.setNavigationIcon(org.khmeracademy.R.drawable.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(org.khmeracademy.R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Event Menu Item Back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Call Navigation drawer
        nvd = new MyNavigationDrawer(this, org.khmeracademy.R.id.nav_view);

        // Put Text to About us activity
        ArticleBuilder amb = new ArticleBuilder();
        amb.append(
                "Khmer Academy គឺ\u200Bជា\u200Bកម្មវិធី\u200B \u200Be-learning\u200B \u200Bដំបូង\u200Bបង្អស់\u200Bសម្រាប់\u200Bចែក\u200Bរំលែក" +
                        "\u200Bនូវ\u200Bចំនេះ\u200Bដឹង\u200Bនៅ\u200Bក្នុង\u200Bប្រទេស\u200Bកម្ពុជា\u200B។\u200B e-learning\u200B \u200Bមួយ" +
                        "\u200Bនេះ\u200Bបាន\u200Bបង្កើត\u200Bឡើង\u200Bដោយ\u200Bមជ្ឈមណ្ឌល\u200B\u200Bកូរ៉េ\u200B អេច\u200B\u200B\u200B " +
                        "\u200Bអ\u200B \u200B\u200Bឌី\u200B \u200Bក្នុង\u200Bឆ្នាំ\u200B \u200B2015។\u200B \u200B រាល់\u200Bមេរៀន\u200Bទាំង" +
                        "\u200Bអស់\u200Bត្រូវ\u200Bបាន\u200Bបង្រៀន\u200Bជា\u200Bភាសាខ្មែរ\u200B \u200Bដើម្បី\u200Bផ្តល់\u200Bភាព\u200Bងាយ" +
                        "\u200Bស្រួល\u200Bក្នុង\u200Bការ\u200Bសិក្សា\u200Bស្វែង\u200Bយល់\u200B។\u200B \u200Bមេរៀន\u200Bទាំង\u200Bអស់\u200Bភាគ" +
                        "\u200Bច្រើន\u200Bផ្តោត\u200B\u200Bសំខាន់\u200B\u200Bទៅ\u200Bលើ\u200B \u200Bមុខ\u200Bវិជ្ជា\u200Bព័ត៍មាន\u200Bវិទ្យ" +
                        "ា\u200B \u200Bហើយ\u200Bយើង\u200Bមាន\u200B\u200Bគម្រោង\u200Bនិង\u200Bពង្រីក\u200Bនូវ\u200Bមុខ\u200B\u200Bវិជ្ជា\u200B" +
                        "\u200Bដ៏ទៃ\u200Bទៀត\u200Bឲ្យ\u200Bបាន\u200Bច្រើន\u200B។\u200B \u200Bគោល\u200Bបំណង\u200Bរបស់\u200Bយើង\u200B " +
                        "\u200Bគឺ\u200Bដើម្បី\u200Bធ្វើ\u200Bឲ្យ\u200Bការ\u200Bអប់\u200Bរំ​ ក្នុង\u200Bប្រទេស\u200Bកម្ពុជា\u200Bកាន់\u200Bតែ\u200Bមាន" +
                        "\u200Bភាព\u200Bរីក\u200Bចម្រើន\u200B\u200B\u200B \u200Bដោយ\u200Bបច្ចេកទេស\u200Bព័ត៍មាន\u200Bវិទ្យា\u200B។\u200B " +
                        "\u200BKhmer Academy\u200B \u200Bនិង\u200Bក្លាយ\u200Bជា\u200Bកម្មវិធី\u200Bដ៏\u200Bមាន\u200Bសក្តានុពល\u200B \u200Bដែល" +
                        "\u200Bប្រមូល\u200B\u200Bនិង\u200B\u200B \u200Bចែក\u200Bចាយ\u200Bរាល់\u200Bចំនេះ\u200Bនៅ\u200Bលើ\u200Bប្រព័ន្ធ\u200Bអ៊ីនធឺណែត" +
                        "\u200B។\u200B",
                true, new RelativeSizeSpan(0.8f), new JustifiedSpan(),
                new StyleSpan(Typeface.NORMAL), new ForegroundColorSpan(0xFF555555));

        addDocumentView(amb, DocumentView.FORMATTED_TEXT);

        // Call Event alert dialog for text view developerName ID
        textView = (TextView) findViewById(org.khmeracademy.R.id.developerName);
        textView.setOnClickListener(this);


    }

    // Call to refresh navigation header
    @Override
    protected void onPostResume() {
        super.onPostResume();
        String id = getIntent().getStringExtra("userId");
        try {
            nvd.requestInfoNav();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Event alert dialog
    @Override
    public void onClick(View v) {

        final CharSequence[] developerName = {
                "Mr. Phan Pirang", "Mr. Torn Longdy", "Ms. Eath Manith", "Ms. Sa Sokngim"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Android Developers Team");
        builder.setItems(developerName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                textView.setText(developerName[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public DocumentView addDocumentView(CharSequence article, int type, boolean rtl) {
        final DocumentView documentView = new DocumentView(this, type);
        documentView.getDocumentLayoutParams().setTextColor(0x00000000);
        documentView.getDocumentLayoutParams().setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/Battambang-Regular.ttf"));
        documentView.getDocumentLayoutParams().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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

        LinearLayout articleList = (LinearLayout) findViewById(org.khmeracademy.R.id.articleList);
        articleList.addView(linearLayout);
        return documentView;

    }

    public DocumentView addDocumentView(CharSequence article, int type) {
        return addDocumentView(article, type, false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.findItem(org.khmeracademy.R.id.action_search).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.khmeracademy.R.menu.activity_main_khmeracademy, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("About Us");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}











