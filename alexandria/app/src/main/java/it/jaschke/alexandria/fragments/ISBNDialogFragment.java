package it.jaschke.alexandria.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import it.jaschke.alexandria.R;

/**
 * @author Julio Mendoza on 8/21/15.
 */
public class ISBNDialogFragment extends DialogFragment
{
    private Button btnOk;

    private OnDialogOkListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout layoutRoot = (LinearLayout) inflater.inflate(R.layout.dialog_isbn, container, false);

        final EditText etIsbn = (EditText) layoutRoot.findViewById(R.id.etIsbn);
        etIsbn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.length() == 13) {
                    btnOk.setEnabled(true);
                } else {
                    btnOk.setEnabled(false);
                }
            }
        });

        btnOk = (Button)layoutRoot.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ean = etIsbn.getText().toString();
                listener.onOK(ean);
                dismiss();
            }
        });

        return layoutRoot;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            listener = (OnDialogOkListener)activity;
        }
        catch (ClassCastException e)
        {
            Log.e(ISBNDialogFragment.class.getSimpleName(),
                    activity.getClass().getSimpleName() + " must implement OnDialogOkListener");
        }
    }

    public interface OnDialogOkListener
    {
        void onOK(String ean);
    }
}
