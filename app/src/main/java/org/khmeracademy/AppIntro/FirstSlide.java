package org.khmeracademy.AppIntro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FirstSlide extends Fragment {
    Button mButtonNext;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(org.khmeracademy.R.layout.intro, container, false);
        mButtonNext = (Button) v.findViewById(org.khmeracademy.R.id.next);
        mButtonNext.setVisibility(View.VISIBLE);
        return v;
    }
}
