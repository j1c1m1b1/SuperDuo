package it.jaschke.alexandria.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activities.AddBookActivity;
import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;


public class ListOfBooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String QUERY = "query";
    private static final int LOADER_ID = 10;
    private BookListAdapter bookListAdapter;

    private int position = ListView.INVALID_POSITION;
    private ListView bookList;
    private View rootView;

    public ListOfBooksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        bookListAdapter = new BookListAdapter(getActivity(), cursor, 0);
        rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);

        bookList = (ListView) rootView.findViewById(R.id.listOfBooks);
        bookList.setAdapter(bookListAdapter);

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

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    public void search(String query)
    {
        if(query != null && !query.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putString(QUERY, query);
            restartLoader(bundle);
        }

    }

    private void restartLoader(Bundle bundle){
        getLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        String query = bundle.getString(QUERY);

        if(query != null && !query.isEmpty()){
            final String selection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " +
                    AlexandriaContract.BookEntry.SUBTITLE + " LIKE ?";

            query = "%"+query+"%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{query,query},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() > 0)
        {
            bookListAdapter.swapCursor(data);
            if (position != ListView.INVALID_POSITION) {
                bookList.smoothScrollToPosition(position);
            }
        }
        else
        {
            Snackbar.make(rootView, R.string.not_found, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.books);
    }
}
