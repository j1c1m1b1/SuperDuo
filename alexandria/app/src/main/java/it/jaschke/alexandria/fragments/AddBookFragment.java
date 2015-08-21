package it.jaschke.alexandria.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.connection.BookRequest;
import it.jaschke.alexandria.interfaces.RequestCallbackListener;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.util.Constants;


public class AddBookFragment extends Fragment{

    private View rootView;
    private Book book;

    public AddBookFragment(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(book != null)
        {
            outState.putParcelable(Constants.BOOK, book);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book != null) {
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.setAction(Constants.SAVE_BOOK);
                    bookIntent.putExtra(Constants.BOOK, book);
                    getActivity().startService(bookIntent);
                }
            }
        });

        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(Constants.BOOK))
            {
                book = savedInstanceState.getParcelable(Constants.BOOK);
                refreshUI();
            }
        }

        return rootView;
    }

    public void searchBook(String eanString)
    {
        if (eanString.length() == 10 && !eanString.startsWith("978")) {
            eanString = "978" + eanString;
        }
        if (eanString.length() == 13)
        {
            RequestCallbackListener listener = new RequestCallbackListener() {
                @Override
                public void onFail() {
                    //TODO Connection error.
                }

                @Override
                public void onSuccess(Response response)
                {
                    try
                    {
                        String bookJsonString = response.body().string();
                        book = BookRequest.JSONToBook(bookJsonString, getActivity());
                        if(book != null)
                        {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshUI();
                                }
                            });
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (JSONException e) {
                        //TODO Bad server response
                        e.printStackTrace();
                    }
                }
            };
            BookRequest.fetchBook(eanString, listener);
        }
    }

    @UiThread
    public void refreshUI()
    {
        String bookTitle = book.getTitle();
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookDescription = book.getDescription();
        ((TextView) rootView.findViewById(R.id.tvDesc)).setText(bookDescription);
        rootView.findViewById(R.id.tvDesc).setVisibility(View.VISIBLE);

        String bookSubTitle = book.getSubtitle();
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = book.getAuthors();
        if(authors != null)
        {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));
        }
        String imgUrl = book.getThumbnail();
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            ImageView bookCover = (ImageView) rootView.findViewById(R.id.bookCover);

            Glide.with(this).load(imgUrl).placeholder(R.drawable.placeholder).into(bookCover);
        }
        rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);

        String categories = book.getCategories();
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
    }

    public void clearFields()
    {
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.tvDesc).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
    }

    public boolean isFilled() {
        return book != null;
    }

    public void showSnackBar(String message)
    {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}
