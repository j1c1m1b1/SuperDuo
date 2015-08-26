package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import barqsoft.footballscores.R;
import barqsoft.footballscores.api.Requests;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.interfaces.RequestCallbackListener;
import barqsoft.footballscores.utils.Constants;

/**
 * @author Julio Mendoza on 8/26/15.
 */
public class FootballScoresSyncAdapter extends AbstractThreadedSyncAdapter
{
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public FootballScoresSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.
                getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
        * Add the account and account type, no password or user data
        * If successful, return the Account object, otherwise report an error.
        */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        FootballScoresSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static void syncImmediately(Context context)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.authority), bundle);

    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              final ContentProviderClient contentProviderClient, SyncResult syncResult)
    {
        Log.i(this.getClass().getSimpleName(), "Starting sync");

        RequestCallbackListener listener = new RequestCallbackListener() {
            @Override
            public void onResponse(ContentValues[] values, @Constants.MatchStatus int status)
            {
                if(status != Constants.RESULT_CODE_SUCCESS)
                {
                    //TODO process erred response;
                }
                else
                {
                    try
                    {
                        contentProviderClient.bulkInsert(DatabaseContract.BASE_CONTENT_URI, values);
                    }
                    catch (RemoteException e)
                    {
                        Log.e(this.getClass().getSimpleName(), "Sync failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        };

        Requests.fetchMatchData(Constants.N2, listener);
        Requests.fetchMatchData(Constants.P2, listener);
    }
}
