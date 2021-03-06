package barqsoft.footballscores.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapters.ScoresAdapter;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.interfaces.OnShareButtonClickListener;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.VerticalSpaceItemDecoration;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int SCORES_LOADER = 0;
    public ScoresAdapter adapter;
    private String fragmentDate;
    private int selectedMatchId = RecyclerView.NO_POSITION;
    private RecyclerView scoreList;
    private ProgressBar pbLoading;
    private TextView tvEmptyView;

    public static MainScreenFragment newInstance(String fragmentDate)
    {
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_DATE, fragmentDate);
        MainScreenFragment fragment = new MainScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentDate = getArguments().getString(Constants.FRAGMENT_DATE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        CoordinatorLayout rootView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_main, container, false);
        scoreList = (RecyclerView) rootView.findViewById(R.id.scores_list);
        pbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        scoreList.setLayoutManager(layoutManager);
        scoreList.setHasFixedSize(true);
        scoreList.setItemAnimator(new DefaultItemAnimator());
        scoreList.addItemDecoration(new VerticalSpaceItemDecoration());

        adapter = new ScoresAdapter(getActivity());
        scoreList.setAdapter(adapter);

        adapter.setOnShareButtonClickListener(new OnShareButtonClickListener() {
            @Override
            public void onItemClick(String shareText) {
                Intent intent = createShareIntent(shareText);
                startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
            }
        });

        return rootView;
    }

    private Intent createShareIntent(String shareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }

    private void showDetail()
    {
        Log.d(MainScreenFragment.class.getSimpleName(), "Showing Detail " + selectedMatchId);
        int position = -1;
        boolean found = false;
        for(int i = 0; i < adapter.getItemCount() && !found; i ++)
        {
            if(adapter.getItemId(i) == selectedMatchId)
            {
                found = true;
                position = i;
                adapter.setDetailMatchId(selectedMatchId);
            }
        }
        scoreList.smoothScrollToPosition(position);
        adapter.notifyItemChanged(position);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        pbLoading.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{fragmentDate}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        pbLoading.setVisibility(View.GONE);
        adapter.swapCursor(cursor);
        if(cursor == null || cursor.getCount() == 0)
        {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
        else
        {
            tvEmptyView.setVisibility(View.GONE);
            SharedPreferences prefs = getActivity()
                    .getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

            selectedMatchId = prefs.getInt(Constants.SELECTED_MATCH, RecyclerView.NO_POSITION);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            if(selectedMatchId != RecyclerView.NO_POSITION)
            {
                showDetail();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        adapter.swapCursor(null);
    }
}
