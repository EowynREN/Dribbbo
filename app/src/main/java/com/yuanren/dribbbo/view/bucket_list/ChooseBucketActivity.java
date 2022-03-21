package com.yuanren.dribbbo.view.bucket_list;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.view.base.SingleFragmentActivity;

import java.util.ArrayList;

public class ChooseBucketActivity extends SingleFragmentActivity {
    @NonNull
    @Override
    protected Fragment newFragment() {
        ArrayList<String> chosenBucketIds = getIntent().getStringArrayListExtra(
                BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
        return BucketListFragment.newInstance(true, chosenBucketIds);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
