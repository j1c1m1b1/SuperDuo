package it.jaschke.alexandria.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Julio Mendoza on 8/24/15.
 */
public class ServerStatus {

    //Book Search API
    public static final int BOOK_STATUS_SUCCESS = 0;
    public static final int BOOK_SERVER_STATUS_DOWN = 1;
    public static final int BOOK_SERVER_STATUS_INVALID = 2;
    public static final int BOOK_STATUS_NOT_FOUND = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BOOK_STATUS_SUCCESS, BOOK_SERVER_STATUS_DOWN, BOOK_SERVER_STATUS_INVALID,
            BOOK_STATUS_NOT_FOUND})
    public @interface BookStatus{}
}
