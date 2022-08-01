package co.tinode.tindroid.push;

import android.content.Context;
import android.text.TextUtils;
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
            if (TextUtils.equals(platformName, "mi")) {
                platformName = "xiaomi";
            }
            TindroidApp.getTinodeCache().setDeviceToken(regId, platformName);
            Log.d("Tinode", "Registered with " + platformName + ": " + regId);
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MixPushMessage message) {

    }
}
