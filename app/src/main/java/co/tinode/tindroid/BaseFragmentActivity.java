package co.tinode.tindroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction tx = fm.beginTransaction()
                .replace(R.id.contentFragment, getFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        tx.commitAllowingStateLoss();
    }

    abstract Fragment getFragment();

    @Override
    int getLayoutRes() {
        return R.layout.activity_base_fragment;
    }
}
