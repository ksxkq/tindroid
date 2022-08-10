package co.tinode.tindroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class AccountInfoSettingActivity extends BaseFragmentActivity {

    static final String FRAGMENT_ACC_PERSONAL = "acc_personal";
    static final String FRAGMENT_ACC_NOTIFICATIONS = "acc_notifications";
    static final String FRAGMENT_ACC_SECURITY = "acc_security";

    @Override
    String getTitleString() {
        return getIntent().getStringExtra("title");
    }

    @Override
    Fragment getFragment() {
        String type = getIntent().getStringExtra("type");
        switch (type) {
            case FRAGMENT_ACC_PERSONAL:
                return new AccPersonalFragment();
            case FRAGMENT_ACC_NOTIFICATIONS:
               return new AccNotificationsFragment();
            case FRAGMENT_ACC_SECURITY:
                return new AccSecurityFragment();
        }
        return null;
    }

    public static void start(Activity activity, String type, @StringRes int titleRes) {
        Intent intent = new Intent(activity, AccountInfoSettingActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("title", activity.getResources().getString(titleRes));
        activity.startActivity(intent);
    }
}
