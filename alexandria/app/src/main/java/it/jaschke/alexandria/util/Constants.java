package it.jaschke.alexandria.util;

/**
 * @author Julio Mendoza on 8/18/15.
 */
public class Constants {

    public static final String SAVE_BOOK = "it.jaschke.alexandria.services.action.SAVE_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";
    public static final String EAN = "EAN";
    public static final String BOOK = "book";
    //Book List
    public static final String ADDED = "added";
    public static final String LIST_BOOKS = "list_books";
    public static final String CHOOSE_VISIBLE = "choose_visible";
    public static final String DIALOG = "dialog";
    //Message Broadcast
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    //Book Request
    public static final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static final String QUERY_PARAM = "q";
    public static final String ISBN_PARAM = "isbn:%s";


    //Book Detail
    public static final String EAN_KEY = "EAN";
    public static final int DETAIL_LOADER_ID = 10;
    public static final String QUERY = "query";
    public static final int BOOKS_LOADER_ID = 10;
    //Animation
    public static final long DEFAULT_DURATION = 500;
    //Add Book
    private static final String EAN_CONTENT="eanContent";
}
