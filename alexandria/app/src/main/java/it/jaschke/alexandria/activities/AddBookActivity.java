package it.jaschke.alexandria.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import it.jaschke.alexandria.R;

/**
 * @author Julio Mendoza on 8/13/15.
 */
public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        setTitle(R.string.add_book);
    }
}
