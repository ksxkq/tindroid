package co.tinode.tindroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragmentContainerActivity extends BaseFragmentActivity {

    @Override
    String getTitleString() {
        return getIntent().getStringExtra("title");
    }

    @Override
    Fragment getFragment() {
        try {
            String fragmentName = getIntent().getStringExtra("fragmentName");
            Class<? extends Fragment> fragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);
            Fragment fragment = fragmentClass.newInstance();
            Bundle args = getIntent().getBundleExtra("args");
            if (args != null) {
                fragment.setArguments(args);
            }
            return fragment;
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Fragment();
    }

    public static void startFragment(Activity activity, Class<? extends Fragment> fragmentClass, @NonNull String title, @Nullable Bundle args) {
        Intent intent = new Intent(activity, BaseFragmentContainerActivity.class);
        intent.putExtra("fragmentName", fragmentClass.getName());
        intent.putExtra("title", title);
        if (args != null) {
            intent.putExtra("args", args);
        }
        activity.startActivity(intent);
    }
}
