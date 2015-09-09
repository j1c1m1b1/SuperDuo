package barqsoft.footballscores.bus.events;

import android.support.annotation.StringRes;

/**
 * @author Julio Mendoza on 9/9/15.
 */
public class ServerDownEvent {

    @StringRes
    private int resId;

    public ServerDownEvent(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
