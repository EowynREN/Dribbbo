package com.yuanren.dribbbo.view.shot_detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yuanren.dribbbo.view.base.SingleFragmentActivity;

public class ShotActivity extends SingleFragmentActivity {

    public final static String KEY_SHOT_TITLE = "shot_title";


    @NonNull
    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }
}
