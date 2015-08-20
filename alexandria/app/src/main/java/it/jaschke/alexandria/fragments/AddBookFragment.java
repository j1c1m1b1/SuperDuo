package it.jaschke.alexandria.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activities.CaptureActivityAlexandria;
import it.jaschke.alexandria.connection.BookRequest;
import it.jaschke.alexandria.interfaces.RequestCallbackListener;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.util.Constants;


public class AddBookFragment extends Fragment{

    private EditText etEan;
    private View rootView;
    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";
    private Book book;
    private boolean filled;

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
        etEan = (EditText) rootView.findViewById(R.id.ean);

        etEan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    searchBook(etEan.getText().toString());
                }
                return false;
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.
                CharSequence text = "This button should let you scan a book for its barcode!";
                Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT).show();
                */

                IntentIntegrator intentIntegrator =
                        IntentIntegrator.forSupportFragment(AddBookFragment.this);
                intentIntegrator.setCaptureActivity(CaptureActivityAlexandria.class);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt(getString(R.string.scan_prompt));
                intentIntegrator.initiateScan();
            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book != null) {
                    clearFields();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                data);
        if(scanningResult != null)
        {
            Snackbar.make(rootView, scanningResult.getContents(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void searchBook(String eanString)
    {
        if (eanString.length() == 10 && !eanString.startsWith("978")) {
            eanString = "978" + eanString;
        }
        if (eanString.length() == 13)
        {
            InputMethodManager imm = (InputMethodManager)getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(etEan.getWindowToken(), 0);

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
        filled = true;
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
        filled = false;
        etEan.setText("");
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
    }

    public boolean isFilled() {
        return filled;
    }

    public void showSnackBar(String message)
    {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}
