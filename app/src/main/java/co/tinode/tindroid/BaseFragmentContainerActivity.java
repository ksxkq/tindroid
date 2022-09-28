package co.tinode.tindroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.tinode.tinodesdk.PromisedReply;
import co.tinode.tinodesdk.model.ServerMessage;

public class BaseFragmentContainerActivity extends BaseFragmentActivity implements ImageViewFragment.AvatarCompletionHandler {

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

    @Override
    public void onAcceptAvatar(String topicName, Bitmap avatar) {
        if (isDestroyed() || isFinishing()) {
            return;
        }

        UiUtils.updateAvatar(Cache.getTinode().getMeTopic(), avatar).thenApply(new PromisedReply.SuccessListener<ServerMessage>() {
            @Override
            public PromisedReply<ServerMessage> onSuccess(ServerMessage result) {
                finish();
                return null;
            }
        });
    }
}
