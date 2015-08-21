package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.util.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    public BookService() {
        super("Alexandria");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(Constants.SAVE_BOOK.equals(action))
            {
                String ean = intent.getStringExtra(Constants.EAN);
                Book book = intent.getParcelableExtra(Constants.BOOK);
                saveBook(ean, book);
            }
            else if (Constants.DELETE_BOOK.equals(action))
            {
                final String ean = intent.getStringExtra(Constants.EAN);
                deleteBook(ean);
            }
        }
    }

    private void saveBook(String ean, Book book) {

        String title = book.getTitle();
        String subtitle = book.getSubtitle();
        String description = book.getDescription();
        String thumbnail = book.getThumbnail();
        String authors = book.getAuthors();
        String categories = book.getCategories();

        writeBackBook(ean, title, subtitle, description,
                thumbnail);

        saveAuthors(ean, authors);

        saveCategories(ean, categories);

        Intent messageIntent = new Intent(Constants.MESSAGE_EVENT);
        messageIntent.putExtra(Constants.MESSAGE_KEY,getResources().getString(R.string.book_saved));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
    }

    private void saveAuthors(String ean, String authors)
    {
        if(authors != null && !authors.isEmpty())
        {
            ContentValues v = new ContentValues();
            if(authors.contains(","))
            {
                String[] authorList = authors.split(",");
                ContentValues[] values = new ContentValues[authorList.length];
                for(int i = 0; i < authorList.length; i ++)
                {
                    v.put(AlexandriaContract.AuthorEntry._ID, ean);
                    v.put(AlexandriaContract.AuthorEntry.AUTHOR, authorList[i].trim());
                    values[i] = v;
                    v.clear();
                }
                getContentResolver().bulkInsert(AlexandriaContract.AuthorEntry.CONTENT_URI, values);
            }
            else
            {
                getContentResolver().insert(AlexandriaContract.AuthorEntry.CONTENT_URI, v);
            }
        }
    }

    private void saveCategories(String ean, String categories)
    {
        if(categories != null && !categories.isEmpty())
        {
            ContentValues v = new ContentValues();
            if(categories.contains(","))
            {
                String[] categoriesList = categories.split(",");
                ContentValues[] values = new ContentValues[categoriesList.length];
                for(int i = 0; i < categoriesList.length; i ++)
                {
                    v.put(AlexandriaContract.AuthorEntry._ID, ean);
                    v.put(AlexandriaContract.AuthorEntry.AUTHOR, categoriesList[i]);
                    values[i] = v;
                    v.clear();
                }
                getContentResolver().bulkInsert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            }
            else
            {
                getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, v);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String ean) {
        if(ean!=null) {
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)), null, null);
        }
    }

    private void writeBackBook(String ean, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values= new ContentValues();
        values.put(AlexandriaContract.BookEntry._ID, ean);
        values.put(AlexandriaContract.BookEntry.TITLE, title);
        values.put(AlexandriaContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(AlexandriaContract.BookEntry.SUBTITLE, subtitle);
        values.put(AlexandriaContract.BookEntry.DESC, desc);
        getContentResolver().insert(AlexandriaContract.BookEntry.CONTENT_URI,values);
    }
}