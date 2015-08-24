package it.jaschke.alexandria.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.bus.BusProvider;
import it.jaschke.alexandria.bus.events.DatabaseChangedEvent;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.util.Constants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

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

        Uri uri = AlexandriaContract.BookEntry.CONTENT_URI;

        String where = AlexandriaContract.BookEntry.EAN + " LIKE ?";
        String[] args = new String[]{ean};
        Cursor cursor = getContentResolver().query(uri, new String[]{AlexandriaContract.BookEntry._ID}, where, args, null);

        if(cursor == null || cursor.getCount() == 0)
        {
            try
            {
                cursor.close();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
            writeBackBook(ean, title, subtitle, description,
                    thumbnail);

            saveAuthors(ean, authors);

            saveCategories(ean, categories);

            BusProvider.postOnMain(new DatabaseChangedEvent(getString(R.string.book_saved)));
        }
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
                v.put(AlexandriaContract.AuthorEntry._ID, ean);
                v.put(AlexandriaContract.AuthorEntry.AUTHOR, authors.trim());
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
                    v.put(AlexandriaContract.CategoryEntry._ID, ean);
                    v.put(AlexandriaContract.CategoryEntry.CATEGORY, categoriesList[i].trim());
                    values[i] = v;
                    v.clear();
                }
                getContentResolver().bulkInsert(AlexandriaContract.CategoryEntry.CONTENT_URI, values);
            }
            else
            {
                v.put(AlexandriaContract.CategoryEntry._ID, ean);
                v.put(AlexandriaContract.CategoryEntry.CATEGORY, categories.trim());
                getContentResolver().insert(AlexandriaContract.CategoryEntry.CONTENT_URI, v);
            }
        }
    }

    private void deleteBook(String ean) {
        if(ean!=null) {
            String where = AlexandriaContract.BookEntry._ID + " = ?";
            String[] args = new String[]{ean};
            getContentResolver().delete(AlexandriaContract.BookEntry.buildBookUri(Long.parseLong(ean)),
                    where, args);

            BusProvider.postOnMain(new DatabaseChangedEvent(getString(R.string.book_deleted)));
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