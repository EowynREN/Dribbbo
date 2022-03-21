package com.yuanren.dribbbo.view.bucket_list;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.view.base.SingleFragmentActivity;
import com.yuanren.dribbbo.view.shot_list.ShotListFragment;

public class BucketShotListActivity extends SingleFragmentActivity {
    public static final String KEY_BUCKET_NAME = "bucketName";

    @NonNull
    @Override
    protected Fragment newFragment() {
        String bucketId = getIntent().getStringExtra(ShotListFragment.KEY_BUCKET_ID);

        return bucketId == null
                ? ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR)
                : ShotListFragment.newBucketListInstance(bucketId);
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_BUCKET_NAME);
    }
}
