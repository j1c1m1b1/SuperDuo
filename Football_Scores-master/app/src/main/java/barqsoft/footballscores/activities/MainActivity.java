package barqsoft.footballscores.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.R;
import barqsoft.footballscores.fragments.PagerFragment;
import barqsoft.footballscores.interfaces.ShowSnackBarListener;
import barqsoft.footballscores.receivers.ServerDownReceiver;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;
import barqsoft.footballscores.utils.Constants;

public class MainActivity extends AppCompatActivity implements ShowSnackBarListener
{
    public static int selectedMatchId;

    public static int currentFragment = 2;
    private static CoordinatorLayout rootLayout;
    private PagerFragment pagerFragment;
    private ServerDownReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = (CoordinatorLayout)findViewById(R.id.container);
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


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new ServerDownReceiver();
        receiver.setListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_SERVER_DOWN);

        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void showMySnackBar() {
        Snackbar.make(rootLayout, R.string.server_down, Snackbar.LENGTH_SHORT).show();
    }
}
