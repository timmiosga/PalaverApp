package timmiosga.palaver.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by timmiosga on 13.07.17.
 */

public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        (new NetworkThread(this)).start();
    }
}

