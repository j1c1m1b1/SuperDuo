package it.jaschke.alexandria.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.fragments.BookDetailFragment;
import it.jaschke.alexandria.fragments.ISBNDialogFragment;
import it.jaschke.alexandria.fragments.ListOfBooksFragment;
import it.jaschke.alexandria.util.Constants;


public class MainActivity extends AppCompatActivity implements Callback,
        ISBNDialogFragment.OnDialogOkListener {

    public static boolean IS_TABLET = false;

    private ListOfBooksFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FragmentManager manager = getSupportFragmentManager();

        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int count = manager.getBackStackEntryCount();
                if(count == 0)
                {
                    setTitle(R.string.books);
                }
            }
        });

        listFragment = (ListOfBooksFragment) manager.findFragmentByTag(Constants.LIST_BOOKS);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        else if(id == R.id.search)
        {
            MenuItemCompat.setOnActionExpandListener(item,
                    new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    if(listFragment != null && listFragment.isVisible())
                    {
                        listFragment.resetLoader();
                    }
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String ean, String bookTitle) {
        Bundle args = new Bundle();
        args.putString(Constants.EAN_KEY, ean);

        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.detail_container) != null){
            id = R.id.detail_container;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack("Book Detail")
                .commit();
    }

    public void isbnDialog()
    {
        ISBNDialogFragment dialogFragment = new ISBNDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), Constants.DIALOG);
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if(listFragment != null && listFragment.isVisible() && listFragment.isChooseActionVisible())
        {
            listFragment.hideChooseActionLayout();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onOK(String ean) {
        listFragment.goToAdd(ean);
    }
}