package org.khmeracademy.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Longdy on 1/17/2016.
 */
public class RefreshView extends FrameLayout {
    private ImageView mButton;
    private ProgressBar mProgressBar;
    private boolean mLoading;

    public RefreshView(Context context) {
        super(context, null);
        initView(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView(context);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View v = inflator.inflate(org.khmeracademy.R.layout.actionbar_refresh, this);
        mProgressBar = (ProgressBar) v.findViewById(org.khmeracademy.R.id.action_refresh_progress);
        mButton = (ImageView) v.findViewById(org.khmeracademy.R.id.action_refresh_button);
    }

    public void setLoading(boolean loading) {
        if (loading != mLoading) {
            mProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            mButton.setVisibility(loading ? View.GONE : View.VISIBLE);
            mLoading = loading;
        }
    }
}
