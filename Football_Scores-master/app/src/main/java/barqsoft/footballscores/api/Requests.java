package barqsoft.footballscores.api;

import android.content.ContentValues;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.interfaces.RequestCallbackListener;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.Utils;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public class Requests {

    private static final String TAG = Requests.class.getSimpleName();

    private static final OkHttpClient client = new OkHttpClient();

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

    public static void fetchMatchData(String timeFrame, String token,
                                      final RequestCallbackListener callbackListener)
    {
        HttpUrl url = HttpUrl.parse(Constants.BASE_URL);

        url = url.newBuilder()
                .addQueryParameter(Constants.QUERY_TIME_FRAME, timeFrame)
                .build();

        Request request;
        if(token != null)
        {
            request = new Request.Builder()
                    .addHeader(Constants.TOKEN_HEADER, token)
                    .url(url)
                    .build();

        }
        else
        {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "" + e.getMessage());
                callbackListener.onResponse(null, Constants.RESULT_CODE_SERVER_DOWN);
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                String JSONData = response.body().string();
                dateFormat.setTimeZone(TimeZone.getTimeZone(Constants.UTC));
                processJSONData(JSONData, callbackListener);
            }
        });
    }

    public static void processJSONData(String JSONData, RequestCallbackListener callbackListener)
    {
        try
        {
            Log.d(TAG, "" + JSONData);

            JSONObject jsonObject = new JSONObject(JSONData);

            JSONArray matches = jsonObject.getJSONArray(Constants.FIXTURES);
            //ContentValues to be inserted
            ArrayList<ContentValues> valuesList = new ArrayList<>();

            ContentValues matchValues;
            ContentValues emptyValues = new ContentValues();
            for(int i = 0;i < matches.length();i++)
            {
                matchValues = new ContentValues();
                JSONObject matchData = matches.getJSONObject(i);
                matchValues = parseMatchData(matchData, matchValues);
                if(matchValues.equals(emptyValues))
                {
                    continue;
                }
                valuesList.add(matchValues);
            }

            ContentValues[] values = valuesList.toArray(new ContentValues[valuesList.size()]);
            if(values.length > 0)
            {
                callbackListener.onResponse(values, Constants.RESULT_CODE_SUCCESS);
            }
            else
            {
                callbackListener.onResponse(null, Constants.RESULT_CODE_NO_DATA);
            }
        }
        catch (JSONException e)
        {
            Log.e(TAG, e.getMessage());

        }
    }

    private static ContentValues parseMatchData(JSONObject matchData, ContentValues matchValues)
            throws JSONException
    {
        String league;
        String mDate;
        String mTime = "00:00";
        String home;
        String away;
        String homeGoals;
        String awayGoals;
        String matchId;
        String matchDay;
        league = matchData.getJSONObject(Constants.LINKS).getJSONObject(Constants.SOCCER_SEASON).
                getString(Constants.HREF);
        league = league.replace(Constants.SEASON_LINK, "");

        if(     league.equals(Constants.PREMIER_LEGAUE) ||
                league.equals(Constants.SERIE_A) ||
                league.equals(Constants.CHAMPIONS_LEAGUE) ||
                league.equals(Constants.BUNDESLIGA) ||
                league.equals(Constants.PRIMERA_DIVISION) ||
                league.equals(Constants.BUNDESLIGA2) ||
                league.equals(Constants.BUNDESLIGA3) ||
                league.equals(Constants.LIGUE1) ||
                league.equals(Constants.LIGUE2) ||
                league.equals(Constants.PREMIER_LEAGUE) ||
                league.equals(Constants.SEGUNDA_DIVISION) ||
                league.equals(Constants.BUNDESLIGA1) ||
                league.equals(Constants.PRIMERA_LIGA) ||
                league.equals(Constants.EREDIVISIE))
        {
            matchId = matchData.getJSONObject(Constants.LINKS).getJSONObject(Constants.SELF).
                    getString("href");
            matchId = matchId.replace(Constants.MATCH_LINK, "");

            mDate = matchData.getString(Constants.MATCH_DATE);

            try
            {
                String[] dateFields = Utils.parseDate(dateFormat, mDate);
                mDate = dateFields[0];
                mTime = dateFields[1];
            }
            catch (ParseException e)
            {
                Log.e(TAG,"" + e.getMessage());
                e.printStackTrace();
            }

            home = matchData.getString(Constants.HOME_TEAM);
            away = matchData.getString(Constants.AWAY_TEAM);
            homeGoals = matchData.getJSONObject(Constants.RESULT).getString(Constants.HOME_GOALS);
            awayGoals = matchData.getJSONObject(Constants.RESULT).getString(Constants.AWAY_GOALS);
            matchDay = matchData.getString(Constants.MATCH_DAY);

            matchValues.put(DatabaseContract.scores_table.MATCH_ID, matchId);
            matchValues.put(DatabaseContract.scores_table.DATE_COL, mDate);
            matchValues.put(DatabaseContract.scores_table.TIME_COL, mTime);
            matchValues.put(DatabaseContract.scores_table.HOME_COL, home);
            matchValues.put(DatabaseContract.scores_table.AWAY_COL, away);
            matchValues.put(DatabaseContract.scores_table.HOME_GOALS_COL, homeGoals);
            matchValues.put(DatabaseContract.scores_table.AWAY_GOALS_COL, awayGoals);
            matchValues.put(DatabaseContract.scores_table.LEAGUE_COL, league);
            matchValues.put(DatabaseContract.scores_table.MATCH_DAY, matchDay);
        }
        return matchValues;
    }
}
