package com.yuanren.dribbbo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanren.dribbbo.R;
import com.yuanren.dribbbo.dribbble.Dribbble;
import com.yuanren.dribbbo.model.User;
import com.yuanren.dribbbo.utils.ImageUtils;
import com.yuanren.dribbbo.view.bucket_list.BucketListFragment;
import com.yuanren.dribbbo.view.shot_list.ShotListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.activity_logout_btn) Button logoutBtn;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // customized action bar
        setSupportActionBar(toolbar);

        // set up back button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setupDrawer();

        // avoid duplicate fragment after screen rotation
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR))
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {

        if (Dribbble.isLoggedIn()){
            View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header_logged_in);

            ((TextView) headerView.findViewById(R.id.nav_header_username)).setText(Dribbble.getCurrentUser().getName());

            ImageView userPicture = (ImageView) headerView.findViewById(R.id.nav_header_user_picture);
            ImageUtils.loadUserPicture(this, Dribbble.getCurrentUser().getAvatarUrl(), userPicture);
        }
        else {
            mNavigationView.inflateHeaderView(R.layout.nav_header);
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dribbble.logout(MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // animation for sandwich icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.open_drawer,         /* "open drawer" description */
                R.string.close_drawer         /* "close drawer" description */
        );

        // listener for Navigation Drawer on expand and collapse
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // add listener to nav drawer after clicking to jump to the top fragment
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()){
                    // collapse drawer if the current one is the uer selected drawer to avoid reloading the same content
                    mDrawerLayout.closeDrawers();
                    return true;
                }

                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.drawer_menu_item_home:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR);
                        setTitle(R.string.drawer_menu_home);
                        break;
                    case R.id.drawer_menu_item_likes:
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_LIKED);
                        setTitle(R.string.drawer_menu_likes);
                        break;
                    case R.id.drawer_menu_item_bucket:
                        fragment = BucketListFragment.newInstance(false, null);
                        setTitle(R.string.drawer_menu_buckets);
                        break;
                }

                // find intended fragment，and collapse drawer
                mDrawerLayout.closeDrawers();

                // replace current fragment with user selected fragment
                if (fragment != null){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_container, fragment)
                            .commit();
                    return true;
                }

                return false;
            }
        });


    }
}