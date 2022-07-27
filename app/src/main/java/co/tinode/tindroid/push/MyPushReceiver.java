package co.tinode.tindroid.push;

import android.content.Context;
import android.util.Log;

import com.mixpush.core.MixPushMessage;
import com.mixpush.core.MixPushPlatform;
import com.mixpush.core.MixPushReceiver;

import co.tinode.tindroid.TindroidApp;

public class MyPushReceiver extends MixPushReceiver {
    @Override
    public void onRegisterSucceed(Context context, MixPushPlatform platform) {
        if (platform != null) {
            String platformName = platform.getPlatformName();
            String regId = platform.getRegId();
            TindroidApp.getTinodeCache().setDeviceToken(regId, platformName);
            Log.d("Tinode", "Registered with " + platformName + ": " + regId);
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MixPushMessage message) {

    }
}
