package co.tinode.tindroid;

import androidx.fragment.app.Fragment;

public class FindByIDActivity extends BaseFragmentActivity{
    @Override
    Fragment getFragment() {
        return new AddByIDFragment();
    }
}
