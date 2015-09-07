package barqsoft.footballscores.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.interfaces.OnShareButtonClickListener;
import barqsoft.footballscores.utils.AnimationUtils;
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
    public static final int COL_ID = 8;

    private final Context context;
    private double detailMatchId;

    private Cursor cursor;

    private OnShareButtonClickListener onShareButtonClickListener;

    public ScoresAdapter(Context context)
    {
        this.context = context;
        detailMatchId = -1;
    }

    public void setDetailMatchId(double detailMatchId) {
        this.detailMatchId = detailMatchId;
    }

    public void swapCursor(Cursor cursor)
    {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public void setOnShareButtonClickListener(OnShareButtonClickListener onShareButtonClickListener) {
        this.onShareButtonClickListener = onShareButtonClickListener;
    }


    @Override
    public ScoresAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.scores_list_item, viewGroup, false);
        return new ViewHolder(view, onShareButtonClickListener);
    }

    @Override
    public void onBindViewHolder(ScoresAdapter.ViewHolder viewHolder, int position) {
        cursor.moveToPosition(position);

        String homeName, awayName, date, score;

        int homeCrestResId, awayCrestRestId;

        double matchId;

        matchId = cursor.getDouble(COL_ID);

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

        if(detailMatchId != -1 && detailMatchId == matchId)
        {
            viewHolder.showDetail();
        }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if(cursor == null || cursor.isClosed() || !cursor.moveToPosition(position))
        {
            return -1;
        }
        else
        {
            return (long)cursor.getDouble(COL_ID);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvHomeName;
        private TextView tvAwayName;
        private TextView tvScore;
        private TextView tvDate;
        private ImageView ivHomeCrest;
        private ImageView ivAwayCrest;
        private GridLayout layoutDetail;
        private TextView tvLeague;
        private TextView tvMatchDay;
        private Button btnShare;

        private String shareText;
        private OnShareButtonClickListener listener;

        public ViewHolder(View view, OnShareButtonClickListener listener)
        {
            super(view);
            tvHomeName = (TextView) view.findViewById(R.id.home_name);
            tvAwayName = (TextView) view.findViewById(R.id.away_name);
            tvDate = (TextView) view.findViewById(R.id.data_textview);
            tvScore = (TextView) view.findViewById(R.id.score_textview);
            ivHomeCrest = (ImageView) view.findViewById(R.id.home_crest);
            ivAwayCrest = (ImageView) view.findViewById(R.id.away_crest);
            layoutDetail = (GridLayout) view.findViewById(R.id.layoutDetail);
            tvLeague = (TextView) view.findViewById(R.id.tvLeague);
            tvMatchDay = (TextView) view.findViewById(R.id.tvMatchDay);
            btnShare = (Button) view.findViewById(R.id.btnShare);

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
                    String.format(context.getString(R.string.awayTeamName),
                            awayName);
            tvAwayName.setText(awayName);
            tvAwayName.setContentDescription(contentDescription);

            contentDescription = String.format(context.getString(R.string.datePlayed), date);
            tvDate.setText(date);
            tvDate.setContentDescription(contentDescription);

            tvScore.setText(score);

            ivHomeCrest.setBackgroundResource(homeCrestResId);
            ivAwayCrest.setBackgroundResource(awayCrestRestId);

            tvMatchDay.setText(matchDay);

            tvLeague.setText(league);

            this.shareText = String.format(context.getString(R.string.share_text_format),
                    homeName, score, awayName);

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(shareText);
                }
            });

        }

        public void showDetail()
        {
            if(layoutDetail.getVisibility()== View.GONE)
            {
                AnimationUtils.expand(layoutDetail);
            }
        }

        @Override
        public void onClick(View view)
        {
            if(layoutDetail.getVisibility()== View.GONE)
            {
                AnimationUtils.expand(layoutDetail);
            }
            else
            {
                AnimationUtils.collapse(layoutDetail);
            }
        }
    }

}
