package it.jaschke.alexandria.connection;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import it.jaschke.alexandria.interfaces.RequestCallbackListener;
import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.util.Constants;

/**
 * @author Julio Mendoza on 8/18/15.
 */
public class BookRequest
{
    private static final OkHttpClient client = new OkHttpClient();

    public static void fetchBook(final String ean, final RequestCallbackListener listener)
    {
        String param = String.format(Constants.ISBN_PARAM, ean);

        HttpUrl url = HttpUrl.parse(Constants.FORECAST_BASE_URL);

        url = url.newBuilder()
                .addQueryParameter(Constants.QUERY_PARAM, param)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                listener.onResponse(null, Constants.BOOK_SERVER_STATUS_DOWN);
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                try {
                    int status = Constants.BOOK_STATUS_NOT_FOUND;
                    Book book = JSONToBook(response.body().string());
                    if(book != null)
                    {
                        status = Constants.BOOK_STATUS_SUCCESS;
                    }
                    listener.onResponse(book, status);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onResponse(null, Constants.BOOK_SERVER_STATUS_INVALID);
                }
            }
        });
    }

    public static Book JSONToBook(String bookJsonString) throws JSONException
    {
        Book book;

        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String DESC = "description";
        final String CATEGORIES = "categories";
        final String IMG_URL_PATH = "imageLinks";
        final String IMG_URL = "thumbnail";

        Log.d(BookRequest.class.getSimpleName(), "" + bookJsonString);

        JSONObject bookJson = new JSONObject(bookJsonString);
        JSONArray bookArray;
        if(bookJson.has(ITEMS)){
            bookArray = bookJson.getJSONArray(ITEMS);
            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

            String title = bookInfo.getString(TITLE);

            String subtitle = "";
            if(bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc="";
            if(bookInfo.has(DESC)){
                desc = bookInfo.getString(DESC);
            }

            String thumbnail = "";
            if(bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                thumbnail = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            String authors = getAuthorsOfBook(bookInfo, AUTHORS);

            String categories = getCategoriesOfBook(bookInfo, CATEGORIES);

            book = new Book(title, subtitle, desc, authors, thumbnail, categories);
            return book;
        }
        return null;
    }

    private static String getAuthorsOfBook(JSONObject bookInfo, String AUTHORS) throws JSONException
    {
        String authors = "";
        if(bookInfo.has(AUTHORS))
        {
            JSONArray jsonArray = bookInfo.getJSONArray(AUTHORS);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                if(i == jsonArray.length() - 1)
                {
                    authors += jsonArray.getString(i);
                }
                else
                {
                    authors += jsonArray.getString(i) + ", ";
                }
            }
        }
        return authors;
    }

    private static String getCategoriesOfBook(JSONObject bookInfo, String CATEGORIES) throws JSONException
    {
        String categories = "";
        if(bookInfo.has(CATEGORIES))
        {
            JSONArray jsonArray = bookInfo.getJSONArray(CATEGORIES);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                if(i == jsonArray.length() - 1)
                {
                    categories += jsonArray.getString(i);
                }
                else
                {
                    categories += jsonArray.getString(i) + ", ";
                }
            }
        }
        return categories;
    }


}
