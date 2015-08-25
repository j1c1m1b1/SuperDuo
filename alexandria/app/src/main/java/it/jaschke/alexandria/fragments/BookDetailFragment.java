package it.jaschke.alexandria.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activities.MainActivity;
import it.jaschke.alexandria.bus.BusProvider;
import it.jaschke.alexandria.bus.events.DatabaseChangedEvent;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.util.Constants;


public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private View rootView;
    private String ean;

    private Book book;
    private String bookTitle;

    public BookDetailFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.BOOK))
        {
            book = savedInstanceState.getParcelable(Constants.BOOK);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            ean = arguments.getString(Constants.EAN_KEY);
            if(book == null)
            {
                getLoaderManager().initLoader(Constants.DETAIL_LOADER_ID, null, this);
            }
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(Constants.EAN, ean);
                bookIntent.setAction(Constants.DELETE_BOOK);
                getActivity().startService(bookIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(book != null)
        {
            getActivity().invalidateOptionsMenu();
            refreshUi();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);
        MenuItem itemSearch = menu.findItem(R.id.search);
        if(itemSearch != null)
        {
            itemSearch.setVisible(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + " " + bookTitle);
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Subscribe
    public void databaseChanged(DatabaseChangedEvent event)
    {
        Snackbar snackbar = Snackbar.make(rootView, event.getMessage(), Snackbar.LENGTH_SHORT);

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if(event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        snackbar.show();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));

        book = new Book(bookTitle, bookSubTitle, desc, authors, imgUrl, categories);

        refreshUi();
    }

    private void refreshUi()
    {
        bookTitle = book.getTitle();
        getActivity().setTitle(bookTitle);
        String bookSubTitle = book.getSubtitle();
        String desc = book.getDescription();
        String authors = book.getAuthors();
        String imgUrl = book.getThumbnail();
        String categories = book.getCategories();


        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);

        ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

        ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);

        if(authors != null)
        {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));
        }

        if(Patterns.WEB_URL.matcher(imgUrl).matches()){

            ImageView fullBookCover = (ImageView)rootView.findViewById(R.id.fullBookCover);

            Glide.with(this).load(imgUrl).placeholder(R.drawable.placeholder).into(fullBookCover);
        }

        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.detail_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BOOK, book);
    }
}