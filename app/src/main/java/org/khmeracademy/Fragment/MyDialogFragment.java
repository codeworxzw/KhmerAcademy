package org.khmeracademy.Fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.khmeracademy.R;

/**
 * Created by sok-ngim on 1/25/16.
 */
public class MyDialogFragment extends DialogFragment {
    public static MyDialogFragment newInstance(String video_id) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString("video_id", video_id);
        frag.setArguments(args);
        return frag;
    }
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comment_sample_dialog, container, false);
       //Log.d("msg",getArguments().getString("video_id"));
       getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getDialog().setTitle("Simple Dialog");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
       transaction.replace(R.id.root_frame, CommentFragment.newInstance(getArguments().getString("video_id", "MTk4")));
       transaction.addToBackStack("CommentFragment");
       transaction.commit();
       return rootView;
    }

}
