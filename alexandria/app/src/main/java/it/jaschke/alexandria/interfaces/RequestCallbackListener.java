package it.jaschke.alexandria.interfaces;

import android.support.annotation.Nullable;

import it.jaschke.alexandria.model.Book;
import it.jaschke.alexandria.util.ServerStatus;

/**
 * @author Julio Mendoza on 8/18/15.
 */
public interface RequestCallbackListener {

    void onResponse(@Nullable Book book, @ServerStatus.BookStatus int status);
}
