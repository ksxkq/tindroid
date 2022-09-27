package co.tinode.tindroid;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

public class FindByIDActivity extends BaseFragmentActivity {

    @Override
    String getTitleString() {
        int id = getIntent().getIntExtra("id", R.id.action_add);
        int titleRes = id == R.id.action_add ? R.string.add_friend : R.string.join_group;
        return getResources().getString(titleRes);
    }

    @Override
    Fragment getFragment() {
        AddByIDFragment addByIDFragment = new AddByIDFragment();
        String userId = getIntent().getStringExtra("userId");
        if (!TextUtils.isEmpty(userId)) {
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            addByIDFragment.setArguments(bundle);
        }
        return addByIDFragment;
    }
}
