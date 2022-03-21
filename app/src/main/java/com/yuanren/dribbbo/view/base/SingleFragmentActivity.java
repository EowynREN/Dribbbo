package com.yuanren.dribbbo.view.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.yuanren.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (isBackEnabled()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getActivityTitle());

        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, newFragment())
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBackEnabled() && item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isBackEnabled() {
        return true;
    }

    @NonNull
    protected String getActivityTitle() {
        return "";
    }

    @NonNull
    protected abstract Fragment newFragment();
}
