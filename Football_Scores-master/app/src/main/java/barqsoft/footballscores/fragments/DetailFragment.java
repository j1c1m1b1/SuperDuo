package barqsoft.footballscores.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.Constants;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public class DetailFragment extends DialogFragment
{

    //Fragment Arguments

    private String matchDay;

    private String league;

    private String shareText;

    public static DetailFragment newInstance(String matchDay, String league, String shareText)
    {
        DetailFragment detailFragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putString(Constants.MATCH_DAY_ARG, matchDay);
        args.putString(Constants.LEAGUE_ARG, league);
        args.putString(Constants.SHARE_TEXT_ARG, shareText);

        detailFragment.setArguments(args);

        return detailFragment;
    }

    private Intent createShareIntent(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matchDay = getArguments().getString(Constants.MATCH_DAY_ARG);
        league = getArguments().getString(Constants.LEAGUE_ARG);
        shareText = getArguments().getString(Constants.SHARE_TEXT_ARG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_detail, container);

        TextView tvLeague = (TextView) rootView.findViewById(R.id.tvLeague);

        tvLeague.setText(league);

        TextView tvMatchDay = (TextView) rootView.findViewById(R.id.tvMatchday);

        tvMatchDay.setText(matchDay);

        Button btnShare = (Button) rootView.findViewById(R.id.btnShare);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = createShareIntent(shareText);
                startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.MATCH_DAY_ARG, matchDay);
        outState.putString(Constants.LEAGUE_ARG, league);
        outState.putString(Constants.SHARE_TEXT_ARG, shareText);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && !savedInstanceState.isEmpty())
        {
            matchDay = savedInstanceState.getString(Constants.MATCH_DAY_ARG);
            league = savedInstanceState.getString(Constants.LEAGUE_ARG);
            shareText = savedInstanceState.getString(Constants.SHARE_TEXT_ARG);
        }
    }
}
