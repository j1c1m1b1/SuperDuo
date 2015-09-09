package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.Utils;

/**
 * @author Julio Mendoza on 9/1/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FootballScoresWidgetService extends RemoteViewsService
{

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_ID = 8;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory()
        {
            private Cursor cursor = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged()
            {
                if(cursor != null)
                {
                    cursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();


                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                Date date = new Date(System.currentTimeMillis());
                String fragmentDate = dateFormat.format(date);

                cursor = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        null, null, new String[]{fragmentDate}, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy()
            {
                if(cursor != null)
                {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position))
                {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.item_widget_matches);
                String homeName, awayName, date, score;

                int homeCrestResId, awayCrestRestId, id;

                homeName = cursor.getString(COL_HOME);
                awayName = cursor.getString(COL_AWAY);
                date = cursor.getString(COL_MATCHTIME);
                score = Utils.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS));

                homeCrestResId = Utils.getTeamCrestByTeamName(cursor.getString(COL_HOME));
                awayCrestRestId = Utils.getTeamCrestByTeamName(cursor.getString(COL_AWAY));
                id = (int)cursor.getDouble(COL_ID);

                views.setImageViewResource(R.id.ivHomeCrest, homeCrestResId);
                views.setImageViewResource(R.id.ivAwayCrest, awayCrestRestId);
                views.setTextViewText(R.id.tvHomeName, homeName);
                views.setTextViewText(R.id.tvAwayName, awayName);
                views.setTextViewText(R.id.tvDate, date);
                views.setTextViewText(R.id.tvScore, score);

                Intent intent = new Intent();
                intent.putExtra(Constants.SELECTED_MATCH, id);

                views.setOnClickFillInIntent(R.id.widget_list_item, intent);

                return views;
            }


            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.item_widget_matches);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

        };
    }
}
