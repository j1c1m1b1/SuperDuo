package barqsoft.footballscores.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.interfaces.OnItemClickListener;
import barqsoft.footballscores.utils.Utils;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder>
{
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_MATCHTIME = 2;

    private final Context context;
    public double detailMatchId = 0;

    private Cursor cursor;

    private OnItemClickListener onItemClickListener;

    public ScoresAdapter(Context context)
    {
        this.context = context;
    }

    public void swapCursor(Cursor cursor)
    {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ScoresAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.scores_list_item, viewGroup, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ScoresAdapter.ViewHolder viewHolder, int position) {
        cursor.moveToPosition(position);

        String homeName, awayName, date, score;

        int homeCrestResId, awayCrestRestId;

        homeName = cursor.getString(COL_HOME);
        awayName = cursor.getString(COL_AWAY);
        date = cursor.getString(COL_MATCHTIME);
        score = Utils.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS));

        homeCrestResId = Utils.getTeamCrestByTeamName(cursor.getString(COL_HOME));

        awayCrestRestId = Utils.getTeamCrestByTeamName(cursor.getString(COL_AWAY));

        String matchDay = Utils.getMatchDay(cursor.getInt(COL_MATCHDAY), cursor.getInt(COL_LEAGUE));

        String league = Utils.getLeague(cursor.getInt(COL_LEAGUE));

        viewHolder.bind(homeName, awayName, date, score, homeCrestResId, awayCrestRestId,
                matchDay, league);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvHomeName;
        public TextView tvAwayName;
        public TextView tvScore;
        public TextView tvDate;
        public ImageView ivHomeCrest;
        public ImageView ivAwayCrest;
        private String matchDay;
        private String league;
        private String shareText;
        private OnItemClickListener listener;

        public ViewHolder(View view, OnItemClickListener listener)
        {
            super(view);
            tvHomeName = (TextView) view.findViewById(R.id.home_name);
            tvAwayName = (TextView) view.findViewById(R.id.away_name);
            tvDate = (TextView) view.findViewById(R.id.data_textview);
            tvScore = (TextView) view.findViewById(R.id.score_textview);
            ivHomeCrest = (ImageView) view.findViewById(R.id.home_crest);
            ivAwayCrest = (ImageView) view.findViewById(R.id.away_crest);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        public void bind(String homeName, String awayName, String date, String score,
                         @DrawableRes int homeCrestResId, @DrawableRes int awayCrestRestId,
                         String matchDay, String league)
        {
            tvHomeName.setText(homeName);
            String contentDescription = String.format(context.getString(R.string.homeTeamName), homeName);
            tvHomeName.setContentDescription(contentDescription);

            contentDescription =
                    String.format(context.getString(R.string.awayTeamName), awayName);
            tvAwayName.setText(awayName);
            tvAwayName.setContentDescription(contentDescription);

            contentDescription = String.format(context.getString(R.string.datePlayed), date);
            tvDate.setText(date);
            tvDate.setContentDescription(contentDescription);

            tvScore.setText(score);

            ivHomeCrest.setBackgroundResource(homeCrestResId);
            ivAwayCrest.setBackgroundResource(awayCrestRestId);
            this.matchDay = matchDay;
            this.league = league;

            this.shareText = "Match Results: " + homeName + " " + score + " " + awayName;

        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(matchDay, league, shareText);
        }
    }

}
