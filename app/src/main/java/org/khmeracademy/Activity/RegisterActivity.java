package org.khmeracademy.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.khmeracademy.R;
import org.khmeracademy.TapLayout.LinkButtonToTab;
import org.khmeracademy.TapLayout.LogInSignUp;

import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity implements OnTabSelectListener, LinkButtonToTab {
    private Context mContext = this;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {"Log In", "Sign Up"};
    private SlidingTabLayout tabLayout_2;
    // Spinner element
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        for (String title : mTitles) {
            mFragments.add(LogInSignUp.getInstance(title));
        }

        /*View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.vp);*/

        ViewPager vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout_2 = (SlidingTabLayout) findViewById(R.id.tl_2);

        tabLayout_2.setViewPager(vp);
        tabLayout_2.setOnTabSelectListener(this);

       /* vp.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });*/

        // Gender Drop Down List
        /*spinner = (Spinner) findViewById(R.id.spinnerGender);
        List list = new ArrayList();
        list.add("Male");
        list.add("Female");
        ArrayAdapter dataAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);*/

    }

    @Override
    public void onTabSelect(int position) {
        //Toast.makeText(mContext, "onTabSelect&position--->" + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTabReselect(int position) {
        //Toast.makeText(mContext, "onTabReselect&position--->" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void selectTab(int tabNum) {
        tabLayout_2.setCurrentTab(tabNum);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
