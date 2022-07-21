package co.tinode.tindroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountInfoSettingActivity extends BaseFragmentActivity {

    static final String FRAGMENT_ACC_PERSONAL = "acc_personal";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        initToolbar(title);
    }

    @Override
    Fragment getFragment() {
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case FRAGMENT_ACC_PERSONAL:
                return new AccPersonalFragment();
        }
        return null;
    }

    public static void start(Activity activity, String type, String title) {
        Intent intent = new Intent(activity, AccountInfoSettingActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        activity.startActivity(intent);
    }
}
