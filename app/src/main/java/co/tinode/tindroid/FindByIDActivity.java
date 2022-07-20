package co.tinode.tindroid;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FindByIDActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar(R.string.action_add_contact);
    }

    @Override
    Fragment getFragment() {
        return new AddByIDFragment();
    }
}
