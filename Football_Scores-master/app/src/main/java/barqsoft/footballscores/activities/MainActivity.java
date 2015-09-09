package barqsoft.footballscores.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Subscribe;

import barqsoft.footballscores.R;
import barqsoft.footballscores.bus.BusProvider;
import barqsoft.footballscores.bus.events.ServerDownEvent;
import barqsoft.footballscores.bus.events.ServerUpEvent;
import barqsoft.footballscores.fragments.PagerFragment;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;
import barqsoft.footballscores.utils.Constants;

public class MainActivity extends AppCompatActivity
{
    public static int selectedMatchId;

    public static int currentFragment = 2;
    private PagerFragment pagerFragment;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.container);
        if (savedInstanceState == null) {
            pagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pagerFragment)
                    .commit();
        }
        FootballScoresSyncAdapter.initializeSyncAdapter(this);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Constants.SELECTED_MATCH))
        {
            selectedMatchId = intent.getIntExtra(Constants.SELECTED_MATCH, Constants.INVALID_VALUE);
            Log.d(MainActivity.class.getSimpleName(), "" + selectedMatchId);

            pagerFragment.showDetail(selectedMatchId, 2);
        }

        snackbar = Snackbar.make(rootLayout, R.string.server_down,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FootballScoresSyncAdapter.syncImmediately(MainActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(Constants.CURRENT_PAGER, pagerFragment.viewPager.getCurrentItem());
        outState.putInt(Constants.SELECTED_MATCH, selectedMatchId);
        getSupportFragmentManager().putFragment(outState,
                Constants.PAGER_FRAGMENT_KEY, pagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        currentFragment = savedInstanceState.getInt(Constants.CURRENT_PAGER);
        selectedMatchId = savedInstanceState.getInt(Constants.SELECTED_MATCH);
        pagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                Constants.PAGER_FRAGMENT_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Subscribe
    public void showSnackBar(ServerDownEvent event)
    {
        //Used to show the snack bar in the main thread.
        setTitle(getString(R.string.app_name));
        snackbar.setText(event.getResId());
        if(!snackbar.isShown())
        {
            Log.d(MainActivity.class.getSimpleName(), "showSnackBar");
            snackbar.show();
        }
    }

    @Subscribe
    public void dismissSnackBar(ServerUpEvent event)
    {
        if(snackbar.isShown())
        {
            snackbar.dismiss();
        }
    }
}
