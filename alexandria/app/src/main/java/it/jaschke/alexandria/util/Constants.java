package it.jaschke.alexandria.util;

/**
 * @author Julio Mendoza on 8/18/15.
 */
public class Constants {

    public static final String SAVE_BOOK = "it.jaschke.alexandria.services.action.SAVE_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";
    public static final String EAN = "EAN";
    public static final String BOOK = "book";
    //Message Broadcast
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    private static final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String QUERY_PARAM = "q";
    private static final String ISBN_PARAM = "isbn:%s";
    //Add Book
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";
    private static final int LOADER_ID = 1;
    private static final String EAN_CONTENT="eanContent";
}
