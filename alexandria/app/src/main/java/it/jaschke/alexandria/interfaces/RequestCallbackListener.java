package it.jaschke.alexandria.interfaces;

import com.squareup.okhttp.Response;

/**
 * @author Julio Mendoza on 8/18/15.
 */
public interface RequestCallbackListener {

    void onFail();

    void onSuccess(Response response);

}
