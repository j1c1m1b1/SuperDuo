package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.data.DatabaseContract;

/**
 * @author Julio Mendoza on 9/1/15.
 */
public class FootballScoresWidgetService extends IntentService
{
    public FootballScoresWidgetService()
    {
        super(FootballScoresWidgetService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FootballScoresWidgetProvider.class));


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Date date = new Date(System.currentTimeMillis());
        String fragmentDate = dateFormat.format(date);

        Cursor cursor = getContentResolver().query( DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{fragmentDate}, null);

        if(cursor != null && cursor.moveToFirst())
        {

        }

    }
}
