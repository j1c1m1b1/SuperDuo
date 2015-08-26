package barqsoft.footballscores.fragments;

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
import barqsoft.footballscores.activities.MainActivity;
import barqsoft.footballscores.adapters.ScoresAdapter;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.interfaces.OnItemClickListener;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int SCORES_LOADER = 0;
    public ScoresAdapter adapter;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;

    public MainScreenFragment()
    {
    }

    private void update_scores()
    {
        /*
        Intent service_start = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(service_start);
        */
        FootballScoresSyncAdapter.syncImmediately(getActivity());
    }

    public void setFragmentDate(String date)
    {
        fragmentdate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final RecyclerView scoreList = (RecyclerView) rootView.findViewById(R.id.scores_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        scoreList.setLayoutManager(layoutManager);
        scoreList.setHasFixedSize(true);
        scoreList.setItemAnimator(new DefaultItemAnimator());

        adapter = new ScoresAdapter();
        scoreList.setAdapter(adapter);

        adapter.detail_match_id = MainActivity.selectedMatchId;

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String matchDay, String league, String shareText) {
                ((MainActivity)getActivity()).showDetailDialog(matchDay, league, shareText);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(SCORES_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null,null,fragmentdate,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        adapter.swapCursor(cursor);
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        adapter.swapCursor(null);
    }


}
