package barqsoft.footballscores.interfaces;

import android.content.ContentValues;

import barqsoft.footballscores.utils.Constants;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public interface RequestCallbackListener {

    void onResponse(ContentValues[] values, @Constants.MatchStatus int status);
}
