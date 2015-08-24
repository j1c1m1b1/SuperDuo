package it.jaschke.alexandria.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.squareup.otto.Subscribe;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.bus.BusProvider;
import it.jaschke.alexandria.bus.events.DatabaseChangedEvent;
import it.jaschke.alexandria.fragments.AddBookFragment;
import it.jaschke.alexandria.util.Constants;

/**
 * @author Julio Mendoza on 8/13/15.
 */
public class AddBookActivity extends AppCompatActivity {

    private AddBookFragment fragment;

    private String ean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        setTitle(R.string.scan);

        fragment = (AddBookFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentAdd);
        getEan(savedInstanceState);

    }

    private void getEan(Bundle savedInstanceState)
    {
        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.EAN))
        {
            ean = savedInstanceState.getString(Constants.EAN);
        }
        Intent intent = getIntent();
        if(ean == null && intent != null && intent.hasExtra(Constants.EAN))
        {
            ean = intent.getStringExtra(Constants.EAN);
        }
        Log.d(this.getClass().getSimpleName(), "EAN null? = " + String.valueOf(ean == null));
        fragment.setEan(ean);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void databaseChanged(DatabaseChangedEvent event)
    {
        if(fragment != null && fragment.isVisible())
        {
            fragment.showSnackBar(event.getMessage());
        }
    }
}
