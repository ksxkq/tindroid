package co.tinode.tindroid.push;

import android.content.Context;

import com.mixpush.core.MixPushMessage;
import com.mixpush.core.MixPushPlatform;
import com.mixpush.core.MixPushReceiver;

public class MyPushReceiver extends MixPushReceiver {
    @Override
    public void onRegisterSucceed(Context context, MixPushPlatform platform) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, MixPushMessage message) {

    }
}
