package barqsoft.footballscores.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.R;
import barqsoft.footballscores.fragments.DetailFragment;
import barqsoft.footballscores.fragments.PagerFragment;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;
import barqsoft.footballscores.utils.Constants;

public class MainActivity extends AppCompatActivity
{
    public static int selectedMatchId;

    public static int currentFragment = 2;

    private final String saveTag = "Save Test";

    private PagerFragment pagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            pagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pagerFragment)
                    .commit();
        }

        FootballScoresSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDetailDialog(String matchDay, String league, String shareText)
    {
        DetailFragment detailFragment = DetailFragment.newInstance(matchDay, league, shareText);
        detailFragment.show(getSupportFragmentManager(), Constants.DIALOG);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(saveTag,"will save");
        Log.v(saveTag,"fragment: "+String.valueOf(pagerFragment.mPagerHandler.getCurrentItem()));
        Log.v(saveTag,"selected id: "+ selectedMatchId);
        outState.putInt("Pager_Current", pagerFragment.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match", selectedMatchId);
        getSupportFragmentManager().putFragment(outState,"pagerFragment", pagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        Log.v(saveTag,"will retrive");
        Log.v(saveTag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(saveTag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        currentFragment = savedInstanceState.getInt("Pager_Current");
        selectedMatchId = savedInstanceState.getInt("Selected_match");
        pagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"pagerFragment");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
