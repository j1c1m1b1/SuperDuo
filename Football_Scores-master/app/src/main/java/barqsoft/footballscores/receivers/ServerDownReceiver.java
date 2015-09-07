package barqsoft.footballscores.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import barqsoft.footballscores.interfaces.ShowSnackBarListener;
import barqsoft.footballscores.utils.Constants;

/**
 * @author Julio Mendoza on 9/7/15.
 */
public class ServerDownReceiver extends BroadcastReceiver
{
    private ShowSnackBarListener listener;

    public void setListener(ShowSnackBarListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constants.ACTION_SERVER_DOWN))
        {
            listener.showMySnackBar();
        }
    }
}
