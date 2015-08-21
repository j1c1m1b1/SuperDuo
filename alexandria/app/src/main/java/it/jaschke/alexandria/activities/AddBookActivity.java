package it.jaschke.alexandria.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.fragments.AddBookFragment;
import it.jaschke.alexandria.util.Constants;

/**
 * @author Julio Mendoza on 8/13/15.
 */
public class AddBookActivity extends AppCompatActivity {

    private AddBookFragment fragment;

    private MessageReceiver messageReceiver;

    private String ean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        setTitle(R.string.scan);

        getEan(savedInstanceState);

        fragment = (AddBookFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentAdd);
        if(!fragment.isFilled())
        {
            fragment.searchBook(ean);
        }
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(Constants.MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(fragment != null && fragment.isVisible())
            {
                if(intent.hasExtra(Constants.MESSAGE_KEY))
                {
                    String message = intent.getStringExtra(Constants.MESSAGE_KEY);
                    fragment.showSnackBar(message);
                }
            }
        }
    }
}
