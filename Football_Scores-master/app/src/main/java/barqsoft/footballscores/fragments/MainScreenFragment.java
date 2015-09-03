package barqsoft.footballscores.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapters.ScoresAdapter;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.interfaces.OnShareButtonClickListener;
import barqsoft.footballscores.utils.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int SCORES_LOADER = 0;
    public ScoresAdapter adapter;
    private String fragmentDate;
    private int last_selected_item = -1;
    private RecyclerView scoreList;

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        scoreList = (RecyclerView) rootView.findViewById(R.id.scores_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        scoreList.setLayoutManager(layoutManager);
        scoreList.setHasFixedSize(true);
        scoreList.setItemAnimator(new DefaultItemAnimator());

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

    public void showDetail(int selectedMatchId) {
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
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{fragmentDate}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        adapter.swapCursor(null);
    }



}
