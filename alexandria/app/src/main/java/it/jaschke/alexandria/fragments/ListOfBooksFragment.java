package it.jaschke.alexandria.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import it.jaschke.alexandria.util.Utils;


public class ListOfBooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookListAdapter bookListAdapter;

    private int position = RecyclerView.NO_POSITION;
    private RecyclerView bookList;
    private CoordinatorLayout rootView;
    private FloatingActionButton fab;
    private FloatingActionButton btnScan;
    private FloatingActionButton btnIsbn;
    private RelativeLayout layoutChooseAction;
    private boolean chooseActionVisible;
    private boolean searching;
    private TextView emptyView;

    public ListOfBooksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.CHOOSE_VISIBLE))
        {
            chooseActionVisible = savedInstanceState.getBoolean(Constants.CHOOSE_VISIBLE);
        }

        rootView = (CoordinatorLayout) inflater.inflate(R.layout.fragment_list_of_books, container, false);

        bookList = (RecyclerView) rootView.findViewById(R.id.listOfBooks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        bookList.setLayoutManager(layoutManager);
        bookList.setHasFixedSize(true);
        bookList.setItemAnimator(new DefaultItemAnimator());

        emptyView = (TextView) rootView.findViewById(R.id.emptyView);

        bookListAdapter = new BookListAdapter();
        bookListAdapter.initialize(getActivity(), ((Callback) getActivity()));

        bookList.setAdapter(bookListAdapter);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);

        final boolean permissionPendingOrGranted = !prefs.contains(Constants.CAMERA_PERMISSION_GRANTED) ||
                prefs.getBoolean(Constants.CAMERA_PERMISSION_GRANTED, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if(!permissionPendingOrGranted)
        {
            fab.setBackgroundResource(R.drawable.ic_mode_edit);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permissionPendingOrGranted)
                {
                    showChooseActionLayout();
                }
                else
                {
                    ((MainActivity) getActivity()).isbnDialog();
                }
            }
        });

        layoutChooseAction = (RelativeLayout) rootView.findViewById(R.id.layoutChooseAction);

        btnIsbn = (FloatingActionButton) rootView.findViewById(R.id.btnIsbn);
        btnScan = (FloatingActionButton) rootView.findViewById(R.id.btnScan);
        if(permissionPendingOrGranted)
        {
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    ((MainActivity)getActivity()).checkCameraPermissions();
                }
            });

            btnIsbn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).isbnDialog();
                }
            });
        }


        if(chooseActionVisible && permissionPendingOrGranted)
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
        Loader<Cursor> loader = getLoaderManager().getLoader(Constants.BOOKS_LOADER_ID);
        if(loader == null || !loader.isStarted())
        {
            Log.d(ListOfBooksFragment.class.getSimpleName(), "Loader init");
            getLoaderManager().initLoader(Constants.BOOKS_LOADER_ID, null, this);
        }
        else
        {
            getLoaderManager().restartLoader(Constants.BOOKS_LOADER_ID, null, this);
        }
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

    public void scanBook()
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
        if(data != null)
        {
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
        getLoaderManager().destroyLoader(Constants.BOOKS_LOADER_ID);
        if(query != null && !query.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.QUERY, query);
            searching = true;
            getLoaderManager().initLoader(Constants.BOOKS_LOADER_ID, bundle, this);
        }
    }

    private void changeEmptyView(@DrawableRes int drawableId, @StringRes int stringId)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            emptyView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawableId, 0, 0);
        }
        else
        {
            Drawable drawable = Utils.getDrawable(drawableId, getActivity());
            Rect bounds = emptyView.getCompoundDrawables()[1].copyBounds();
            drawable.setBounds(bounds);
            emptyView.setCompoundDrawables(null, drawable, null, null);
        }
        emptyView.setText(stringId);
    }

    private void showEmptyView()
    {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView()
    {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(!data.isClosed())
        {
            if(searching)
            {
                if(data.getCount() == 0)
                {
                    changeEmptyView(R.drawable.not_found, R.string.not_found);
                    Snackbar.make(rootView, R.string.not_found, Snackbar.LENGTH_SHORT).show();
                }
                searching = false;
            }
            else
            {
                changeEmptyView(R.drawable.ic_add_book, R.string.no_books);
            }

            bookListAdapter.setCursor(data);
            if(bookListAdapter.getItemCount() == 0)
            {
                showEmptyView();
            }
            else
            {
                hideEmptyView();
            }
            if (position != ListView.INVALID_POSITION)
            {
                bookList.smoothScrollToPosition(position);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(ListOfBooksFragment.class.getSimpleName(), "Loader Reset");
        bookListAdapter.setCursor(null);
    }

    public void resetLoader() {
        getLoaderManager().restartLoader(Constants.BOOKS_LOADER_ID, null, this);
    }
}
