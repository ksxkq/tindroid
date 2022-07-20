package co.tinode.tindroid;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import co.tinode.tindroid.account.ContactsManager;
import co.tinode.tindroid.account.Utils;
import co.tinode.tindroid.media.VxCard;
import co.tinode.tinodesdk.MeTopic;
import co.tinode.tinodesdk.Tinode;
import co.tinode.tinodesdk.Topic;
import co.tinode.tinodesdk.model.Credential;
import co.tinode.tinodesdk.model.Description;
import co.tinode.tinodesdk.model.MsgServerInfo;
import co.tinode.tinodesdk.model.MsgServerPres;
import co.tinode.tinodesdk.model.PrivateType;
import co.tinode.tinodesdk.model.Subscription;

/**
 * This activity owns 'me' topic.
 */
public class ChatsActivity extends AppCompatActivity
        implements UiUtils.ProgressIndicator, UiUtils.AvatarPreviewer,
        ImageViewFragment.AvatarCompletionHandler {

    private static final String TAG = "ContactsActivity";

    static final String TAG_FRAGMENT_NAME = "fragment";
    static final String FRAGMENT_CHATLIST = "contacts";
    static final String FRAGMENT_ACCOUNT_INFO = "account_info";
    static final String FRAGMENT_AVATAR_PREVIEW = "avatar_preview";
    static final String FRAGMENT_ACC_HELP = "acc_help";
    static final String FRAGMENT_ACC_NOTIFICATIONS = "acc_notifications";
    static final String FRAGMENT_ACC_PERSONAL = "acc_personal";
    static final String FRAGMENT_ACC_SECURITY = "acc_security";
    static final String FRAGMENT_ACC_ABOUT = "acc_about";
    static final String FRAGMENT_ARCHIVE = "archive";
    static final String FRAGMENT_BANNED = "banned";

    private ContactsEventListener mTinodeListener = null;
    private MeListener mMeTopicListener = null;
    private MeTopic<VxCard> mMeTopic = null;

    private Account mAccount;

    private CommonTabLayout mSegmentTabLayout;
    private ViewPager mViewPage; // ViewPage2 has problem with scroll vertical and horizontal
    private String[] mTitles = {"消息", "通讯录", "发现", "我"};
    private int[] mIconUnselectIds = {
            R.drawable.ic_block_gray, R.drawable.ic_block_gray,
            R.drawable.ic_block_gray, R.drawable.ic_block_gray};
    private int[] mIconSelectIds = {
            R.drawable.ic_block_red, R.drawable.ic_block_red,
            R.drawable.ic_block_red, R.drawable.ic_block_red};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);

        setSupportActionBar(findViewById(R.id.toolbar));

        mSegmentTabLayout = findViewById(R.id.bottomBar);
        mViewPage = findViewById(R.id.viewPage);

        mFragments.add(new ChatsFragment());
        mFragments.add(WebViewFragment.newInstance("https://www.baidu.com/"));
        mFragments.add(TextFragment.newInstance(mTitles[2]));
        mFragments.add(TextFragment.newInstance(mTitles[3]));

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mSegmentTabLayout.setTabData(mTabEntities);
        mSegmentTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPage.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        mViewPage.setOffscreenPageLimit(4);
        mViewPage.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSegmentTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPage.setCurrentItem(0);

//        FragmentManager fm = getSupportFragmentManager();

//        if (fm.findFragmentByTag(FRAGMENT_CHATLIST) == null) {
//            Fragment fragment = new ChatsFragment();
//            fm.beginTransaction()
//                    .replace(R.id.contentFragment, fragment, FRAGMENT_CHATLIST)
//                    .setPrimaryNavigationFragment(fragment)
//                    .commit();
//        }

        mMeTopic = Cache.getTinode().getOrCreateMeTopic();
        mMeTopicListener = new MeListener();
    }

    /**
     * onResume restores subscription to 'me' topic and sets listener.
     */
    @Override
    public void onResume() {
        super.onResume();

        final Tinode tinode = Cache.getTinode();
        mTinodeListener = new ContactsEventListener(tinode.isConnected());
        tinode.addListener(mTinodeListener);

        UiUtils.setupToolbar(this, null, null, false, null, false);

        if (!mMeTopic.isAttached()) {
            toggleProgressIndicator(true);
        }

        // This will issue a subscription request.
        if (!UiUtils.attachMeTopic(this, mMeTopicListener)) {
            toggleProgressIndicator(false);
        }

        final Intent intent = getIntent();
        String tag = intent.getStringExtra(TAG_FRAGMENT_NAME);
        if (!TextUtils.isEmpty(tag)) {
            showFragment(tag, null);
        }
    }

    private void datasetChanged() {
        Fragment fragment = UiUtils.getVisibleFragment(getSupportFragmentManager());
        if (fragment instanceof ChatsFragment) {
            ((ChatsFragment) fragment).datasetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Cache.getTinode().removeListener(mTinodeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMeTopic != null) {
            mMeTopic.setListener(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Enable options menu by returning true
        return true;
    }

    @Override
    public void showAvatarPreview(Bundle args) {
        showFragment(FRAGMENT_AVATAR_PREVIEW, args);
    }

    void showFragment(String tag, Bundle args) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            switch (tag) {
                case FRAGMENT_ACCOUNT_INFO:
                    fragment = new AccountInfoFragment();
                    break;
                case FRAGMENT_ACC_HELP:
                    fragment = new AccHelpFragment();
                    break;
                case FRAGMENT_ACC_NOTIFICATIONS:
                    fragment = new AccNotificationsFragment();
                    break;
                case FRAGMENT_ACC_PERSONAL:
                    fragment = new AccPersonalFragment();
                    break;
                case FRAGMENT_AVATAR_PREVIEW:
                    fragment = new ImageViewFragment();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putBoolean(AttachmentHandler.ARG_AVATAR, true);
                    break;
                case FRAGMENT_ACC_SECURITY:
                    fragment = new AccSecurityFragment();
                    break;
                case FRAGMENT_ACC_ABOUT:
                    fragment = new AccAboutFragment();
                    break;
                case FRAGMENT_ARCHIVE:
                case FRAGMENT_BANNED:
                    fragment = new ChatsFragment();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putBoolean(tag, true);
                    break;
                case FRAGMENT_CHATLIST:
                    fragment = new ChatsFragment();
                    break;
                default:
                    throw new IllegalArgumentException("Failed to create fragment: unknown tag " + tag);
            }
        } else if (args == null) {
            // Retain old arguments.
            args = fragment.getArguments();
        }

        if (args != null) {
            if (fragment.getArguments() != null) {
                fragment.getArguments().putAll(args);
            } else {
                fragment.setArguments(args);
            }
        }

        FragmentTransaction trx = fm.beginTransaction();
        trx.replace(R.id.contentFragment, fragment, tag)
                .addToBackStack(tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void toggleProgressIndicator(boolean on) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof UiUtils.ProgressIndicator && (f.isVisible() || !on)) {
                ((UiUtils.ProgressIndicator) f).toggleProgressIndicator(on);
            }
        }
    }

    @Override
    public void onAcceptAvatar(String topicName, Bitmap avatar) {
        if (isDestroyed() || isFinishing()) {
            return;
        }

        UiUtils.updateAvatar(Cache.getTinode().getMeTopic(), avatar);
    }

    interface FormUpdatable {
        void updateFormValues(final FragmentActivity activity, final MeTopic<VxCard> me);
    }

    // This is called on Websocket thread.
    private class MeListener extends UiUtils.MeEventListener {
        private void updateVisibleInfoFragment() {
            runOnUiThread(() -> {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    if (f != null && f.isVisible() && f instanceof FormUpdatable) {
                        ((FormUpdatable) f).updateFormValues(ChatsActivity.this, mMeTopic);
                    }
                }
            });
        }

        @Override
        public void onInfo(MsgServerInfo info) {
            datasetChanged();
        }

        @Override
        public void onPres(MsgServerPres pres) {
            if ("msg".equals(pres.what)) {
                datasetChanged();
            } else if ("off".equals(pres.what) || "on".equals(pres.what)) {
                datasetChanged();
            }
        }

        @Override
        public void onMetaSub(final Subscription<VxCard, PrivateType> sub) {
            if (sub.deleted == null) {
                if (sub.pub != null) {
                    sub.pub.constructBitmap();
                }

                if (!UiUtils.isPermissionGranted(ChatsActivity.this, Manifest.permission.WRITE_CONTACTS)) {
                    // We can't save contact if we don't have appropriate permission.
                    return;
                }

                if (mAccount == null) {
                    mAccount = Utils.getSavedAccount(AccountManager.get(ChatsActivity.this),
                            Cache.getTinode().getMyId());
                }
                if (Topic.isP2PType(sub.topic)) {
                    ContactsManager.processContact(ChatsActivity.this,
                            ChatsActivity.this.getContentResolver(),
                            mAccount, sub.pub, null, sub.getUnique(), sub.deleted != null,
                            null, false);
                }
            }
        }

        @Override
        public void onMetaDesc(final Description<VxCard, PrivateType> desc) {
            if (desc.pub != null) {
                desc.pub.constructBitmap();
            }

            updateVisibleInfoFragment();
        }

        @Override
        public void onSubsUpdated() {
            datasetChanged();
        }

        @Override
        public void onSubscriptionError(Exception ex) {
            runOnUiThread(() -> {
                Fragment fragment = UiUtils.getVisibleFragment(getSupportFragmentManager());
                if (fragment instanceof UiUtils.ProgressIndicator) {
                    ((UiUtils.ProgressIndicator) fragment).toggleProgressIndicator(false);
                }
            });
        }

        @Override
        public void onContUpdated(final String contact) {
            datasetChanged();
        }

        @Override
        public void onMetaTags(String[] tags) {
            updateVisibleInfoFragment();
        }

        @Override
        public void onCredUpdated(Credential[] cred) {
            updateVisibleInfoFragment();
        }
    }

    private class ContactsEventListener extends UiUtils.EventListener {
        ContactsEventListener(boolean online) {
            super(ChatsActivity.this, online);
        }

        @Override
        public void onLogin(int code, String txt) {
            super.onLogin(code, txt);
            UiUtils.attachMeTopic(ChatsActivity.this, mMeTopicListener);
        }

        @Override
        public void onDisconnect(boolean byServer, int code, String reason) {
            super.onDisconnect(byServer, code, reason);

            // Update online status of contacts.
            datasetChanged();
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    private class TabEntity implements CustomTabEntity {

        private String title;
        private int selectIcon;
        private int unselectIcon;

        public TabEntity(String title, int selectIcon, int unselectIcon) {
            this.title = title;
            this.selectIcon = selectIcon;
            this.unselectIcon = unselectIcon;
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public int getTabSelectedIcon() {
            return selectIcon;
        }

        @Override
        public int getTabUnselectedIcon() {
            return unselectIcon;
        }
    }

    public static class TextFragment extends Fragment {

        public static TextFragment newInstance(String text) {
            Bundle args = new Bundle();
            args.putString("txt", text);
            TextFragment fragment = new TextFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            TextView textView = new TextView(getActivity());
            String txt = getArguments().getString("txt");
            textView.setText(txt);
            return textView;
        }
    }

}