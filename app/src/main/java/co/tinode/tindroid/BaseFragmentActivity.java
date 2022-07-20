package co.tinode.tindroid;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
        setStatusBarColor();

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction tx = fm.beginTransaction()
                .replace(R.id.contentFragment, getFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        tx.commitAllowingStateLoss();
    }

    protected void initToolbar(@StringRes int titleRes) {
        initToolbar(getResources().getString(titleRes));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    protected void initToolbar(String title) {
        Toolbar mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setTitle(title);
        mToolbarView.setBackgroundColor(getResources().getColor(R.color.white));
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

    abstract Fragment getFragment();

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.activity_base_fragment;
    }

}
