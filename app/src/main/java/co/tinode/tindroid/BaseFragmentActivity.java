package co.tinode.tindroid;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        UiUtils.initStatusBar(this);

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction tx = fm.beginTransaction()
                .replace(R.id.contentFragment, getFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        tx.commitAllowingStateLoss();
    }

    protected void initToolbar(@StringRes int titleRes) {
        initToolbar(getResources().getString(titleRes));
    }

    protected void initToolbar(String title) {
        Toolbar mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setTitle(title);
        mToolbarView.setBackgroundColor(getResources().getColor(R.color.bg_toolbar));
        setSupportActionBar(mToolbarView);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    abstract Fragment getFragment();

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.activity_base_fragment;
    }

}
