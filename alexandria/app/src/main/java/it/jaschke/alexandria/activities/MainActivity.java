package it.jaschke.alexandria.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import it.jaschke.alexandria.AboutActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.fragments.BookDetailFragment;
import it.jaschke.alexandria.fragments.ListOfBooksFragment;


public class MainActivity extends AppCompatActivity implements Callback {

    private static final String ADDED = "added";

    public static boolean IS_TABLET = false;

    private boolean added;
    private ListOfBooksFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(ADDED))
        {
            added = savedInstanceState.getBoolean(ADDED);
        }
        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        final FragmentManager manager = getSupportFragmentManager();

        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int count = manager.getBackStackEntryCount();
                Log.d(MainActivity.class.getSimpleName(), "Backstack count: " + count);
                if(count == 0)
                {
                    setTitle(R.string.books);
                }
            }
        });

        if(!added)
        {
            setTitle(R.string.books);

            listFragment = new ListOfBooksFragment();

            FragmentTransaction transaction = manager.beginTransaction();

            int id = R.id.container;
            if(findViewById(R.id.right_container) != null){
                id = R.id.right_container;
            }
            transaction.replace(id, listFragment)
                    .commit();
            added = true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getAction().equals(Intent.ACTION_SEARCH) && listFragment != null
                && listFragment.isVisible())
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            listFragment.search(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean, String bookTitle) {
        Bundle args = new Bundle();
        args.putString(BookDetailFragment.EAN_KEY, ean);

        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack("Book Detail")
                .commit();

    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ADDED, added);
    }

}