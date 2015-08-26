package barqsoft.footballscores.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public class FootballScoresAuthenticatorService extends Service {

    private FootballScoresAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new FootballScoresAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
