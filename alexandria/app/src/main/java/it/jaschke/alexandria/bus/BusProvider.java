package it.jaschke.alexandria.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * @author Julio Mendoza on 8/24/15.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();
    private static final Handler mainThread = new Handler(Looper.getMainLooper());

    private BusProvider() {
    }

    public static Bus getInstance() {
        return BUS;
    }

    public static void postOnMain(final Object event) {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                getInstance().post(event);
            }
        });
    }
}
