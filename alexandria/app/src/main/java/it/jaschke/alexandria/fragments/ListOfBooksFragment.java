package it.jaschke.alexandria.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activities.AddBookActivity;
import it.jaschke.alexandria.activities.CaptureActivityAlexandria;
import it.jaschke.alexandria.activities.MainActivity;
import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.util.AnimationUtils;
import it.jaschke.alexandria.util.Constants;


public class ListOfBooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookListAdapter bookListAdapter;

    private int position = ListView.INVALID_POSITION;
    private ListView bookList;
    private View rootView;
    private FloatingActionButton fab;
    private FloatingActionButton btnScan;
    private FloatingActionButton btnIsbn;
    private RelativeLayout layoutChooseAction;
    private boolean chooseActionVisible;
    private boolean searching;

    public ListOfBooksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.CHOOSE_VISIBLE))
        {
            chooseActionVisible = savedInstanceState.getBoolean(Constants.CHOOSE_VISIBLE);
        }

        rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);

        bookList = (ListView) rootView.findViewById(R.id.listOfBooks);
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = bookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {

                    String bookId = cursor.getString(cursor
                            .getColumnIndex(AlexandriaContract.BookEntry._ID));

                    String bookTitle = cursor.getString(cursor
                            .getColumnIndex(AlexandriaContract.BookEntry.TITLE));

                    ((Callback) getActivity()).onItemSelected(bookId, bookTitle);
                }
            }
        });

        bookList.setEmptyView(rootView.findViewById(R.id.emptyView));

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChooseActionLayout();
            }
        });

        layoutChooseAction = (RelativeLayout) rootView.findViewById(R.id.layoutChooseAction);
        btnScan = (FloatingActionButton) rootView.findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBook();
            }
        });

        btnIsbn = (FloatingActionButton) rootView.findViewById(R.id.btnIsbn);
        btnIsbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).isbnDialog();
            }
        });

        if(chooseActionVisible)
        {
            layoutChooseAction.setVisibility(View.VISIBLE);
            btnScan.setVisibility(View.VISIBLE);
            btnIsbn.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(Constants.BOOKS_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.books);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.CHOOSE_VISIBLE, chooseActionVisible);
    }

    private void scanBook()
    {
        IntentIntegrator intentIntegrator =
                IntentIntegrator.forSupportFragment(ListOfBooksFragment.this);
        intentIntegrator.setCaptureActivity(CaptureActivityAlexandria.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt(getString(R.string.scan_prompt));
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);

        //Start scanning
        intentIntegrator.initiateScan();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                data);
        if(scanningResult != null)
        {
            String contents = scanningResult.getContents();
            String formatName = scanningResult.getFormatName();
            if(formatName.equals(BarcodeFormat.EAN_13.name()))
            {
                goToAdd(contents);
            }
            else
            {
                Snackbar.make(rootView, R.string.incorrect_format, Snackbar.LENGTH_LONG).show();
            }
        }
        else
        {
            Snackbar.make(rootView, R.string.no_data, Snackbar.LENGTH_LONG).show();
        }
    }

    public void goToAdd(String ean)
    {
        hideChooseActionLayout();
        Intent intent = new Intent(getActivity(), AddBookActivity.class);
        intent.putExtra(Constants.EAN, ean);
        getActivity().startActivity(intent);
    }

    private void showChooseActionLayout()
    {

        ObjectAnimator anim1 = AnimationUtils.appear(layoutChooseAction);

        ObjectAnimator anim2 = AnimationUtils.appear(btnScan);

        ObjectAnimator anim3 = AnimationUtils.appear(btnIsbn);

        ObjectAnimator anim4 = AnimationUtils.disappear(fab);

        AnimatorSet set = AnimationUtils.playTogether(anim1, anim2, anim4);

        AnimationUtils.playSequentially(set, anim3).start();

        chooseActionVisible = true;
    }

    public void hideChooseActionLayout()
    {
        ObjectAnimator anim1 = AnimationUtils.disappear(layoutChooseAction);

        ObjectAnimator anim2 = AnimationUtils.disappear(btnScan);

        ObjectAnimator anim3 = AnimationUtils.disappear(btnIsbn);

        AnimationUtils.appear(fab).start();

        AnimationUtils.playSequentially(anim1, anim2, anim3).start();

        chooseActionVisible = false;
    }

    public boolean isChooseActionVisible() {
        return chooseActionVisible;
    }

    public void search(String query)
    {
        if(query != null && !query.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.QUERY, query);
            searching = true;
            getLoaderManager().initLoader(Constants.BOOKS_LOADER_ID, bundle, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        Uri uri = AlexandriaContract.BookEntry.CONTENT_URI;
        if(bundle != null)
        {
            String query = bundle.getString(Constants.QUERY);
            if(query != null && !query.isEmpty())
            {
                String selection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " +
                        AlexandriaContract.BookEntry.SUBTITLE + " LIKE ?";

                query = "%"+query+"%";

                return new CursorLoader(getActivity(), uri, null, selection,
                        new String[]{query,query},null);
            }
        }

        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(ListOfBooksFragment.class.getSimpleName(), "Loader finished");
        if(data.getCount() > 0)
        {
            if(bookListAdapter != null)
            {
                bookListAdapter.swapCursor(data);
                if (position != ListView.INVALID_POSITION) {
                    bookList.smoothScrollToPosition(position);
                }
            }
            else
            {
                bookListAdapter = new BookListAdapter(getActivity(), data, 0);
            }
            bookList.setAdapter(bookListAdapter);
        }
        else if(searching)
        {
            Snackbar.make(rootView, R.string.not_found, Snackbar.LENGTH_SHORT).show();
            searching = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }
}
