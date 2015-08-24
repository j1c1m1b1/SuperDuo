package it.jaschke.alexandria.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.connection.BookRequest;
import it.jaschke.alexandria.interfaces.RequestCallbackListener;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.util.Constants;
import it.jaschke.alexandria.util.ServerStatus;


public class AddBookFragment extends Fragment{

    private View rootView;
    private LinearLayout layoutBook;
    private FrameLayout layoutDisconnected;
    private FrameLayout layoutNotFound;
    private FrameLayout layoutLoading;
    private Book book;
    private int status = -1;
    private String ean;

    public AddBookFragment(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(book != null)
        {
            outState.putParcelable(Constants.BOOK, book);
        }
        if(status != -1)
        {
            outState.putInt(Constants.STATUS, status);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book != null) {
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.setAction(Constants.SAVE_BOOK);
                    bookIntent.putExtra(Constants.BOOK, book);
                    bookIntent.putExtra(Constants.EAN, ean);
                    getActivity().startService(bookIntent);
                }
            }
        });

        layoutBook = (LinearLayout) rootView.findViewById(R.id.layoutBook);
        layoutDisconnected = (FrameLayout)rootView.findViewById(R.id.layoutDisconnected);
        layoutNotFound = (FrameLayout)rootView.findViewById(R.id.layoutNotFound);
        layoutLoading = (FrameLayout)rootView.findViewById(R.id.layoutLoading);

        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(Constants.BOOK))
            {
                book = savedInstanceState.getParcelable(Constants.BOOK);
            }
            if(savedInstanceState.containsKey(Constants.STATUS))
            {
                status = savedInstanceState.getInt(Constants.STATUS);
                refreshUI();
            }
        }

        return rootView;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(status == -1)
        {
            searchBook(ean);
        }
    }

    private void searchBook(String eanString)
    {
        if (eanString.length() == 10 && !eanString.startsWith("978")) {
            eanString = "978" + eanString;
        }
        if (eanString.length() == 13)
        {
            RequestCallbackListener listener = new RequestCallbackListener() {
                @Override
                public void onResponse(@Nullable Book book, @ServerStatus.BookStatus final int status)
                {
                    AddBookFragment.this.status = status;
                    AddBookFragment.this.book = book;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUI();
                        }
                    });
                }

            };
            BookRequest.fetchBook(eanString, listener);
        }
    }

    public void refreshUI()
    {
        layoutLoading.setVisibility(View.GONE);
        switch (status)
        {
            case ServerStatus.BOOK_SERVER_STATUS_INVALID:
            case ServerStatus.BOOK_SERVER_STATUS_DOWN:
                layoutDisconnected.setVisibility(View.VISIBLE);
                break;
            case ServerStatus.BOOK_STATUS_NOT_FOUND:
                layoutNotFound.setVisibility(View.VISIBLE);
                break;
            case ServerStatus.BOOK_STATUS_SUCCESS:
                layoutBook.setVisibility(View.VISIBLE);
                fillBookInfo();
                break;
            default:
                break;
        }

    }

    private void fillBookInfo()
    {
        String bookTitle = book.getTitle();
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookDescription = book.getDescription();
        ((TextView) rootView.findViewById(R.id.tvDesc)).setText(bookDescription);

        String bookSubTitle = book.getSubtitle();
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = book.getAuthors();
        if(authors != null)
        {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        }
        String imgUrl = book.getThumbnail();
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            ImageView bookCover = (ImageView) rootView.findViewById(R.id.bookCover);
            Glide.with(this).load(imgUrl).placeholder(R.drawable.placeholder).into(bookCover);
        }

        String categories = book.getCategories();
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);
    }

    public void showSnackBar(String message)
    {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                    getActivity().finish();
                }
            }
        });

        snackbar.show();
    }
}
