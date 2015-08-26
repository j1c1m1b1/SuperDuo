package it.jaschke.alexandria.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Julio Mendoza on 8/18/15.
 */
public class Constants {

    public static final String SAVE_BOOK = "it.jaschke.alexandria.services.action.SAVE_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";
    public static final String EAN = "EAN";
    public static final String BOOK = "book";

    //Book List
    public static final String LIST_BOOKS = "list_books";
    public static final String CHOOSE_VISIBLE = "choose_visible";
    public static final String DIALOG = "dialog";

    //Book Request
    public static final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAM = "q";
    public static final String ISBN_PARAM = "isbn:%s";

    //Book Detail
    public static final String EAN_KEY = "EAN";
    public static final int DETAIL_LOADER_ID = 20;
    public static final String QUERY = "query";
    public static final int BOOKS_LOADER_ID = 10;
    //Animation
    public static final long DEFAULT_DURATION = 500;
    //Add Book
    public static final String STATUS = "status";

    //Book Search API
    /**
     * Status code returned when a book is successfully fetched
     */
    public static final int BOOK_STATUS_SUCCESS = 0;

    /**
     * Status code returned when attempting to get a book and the request fails. Either the server
     * is down or the user is not connected to a network.
     */
    public static final int BOOK_SERVER_STATUS_DOWN = 1;

    /**
     * Status code returned when the data retrieved from the server is invalid.
     */
    public static final int BOOK_SERVER_STATUS_INVALID = 2;

    /**
     * Status code returned when the book with a given ISBN code is not found in the server.
     */
    public static final int BOOK_STATUS_NOT_FOUND = 3;

    /**
     * Describes the book status codes.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BOOK_STATUS_SUCCESS, BOOK_SERVER_STATUS_DOWN, BOOK_SERVER_STATUS_INVALID,
            BOOK_STATUS_NOT_FOUND})
    public @interface BookStatus{}
}
