package barqsoft.footballscores.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public class FootballScoresSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static FootballScoresSyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {

        synchronized (sSyncAdapterLock)
        {
            if(syncAdapter == null)
            {
                syncAdapter = new FootballScoresSyncAdapter(getApplicationContext(), true);
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
