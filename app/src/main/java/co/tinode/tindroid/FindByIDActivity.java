package co.tinode.tindroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
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
        return new AddByIDFragment();
    }
}
