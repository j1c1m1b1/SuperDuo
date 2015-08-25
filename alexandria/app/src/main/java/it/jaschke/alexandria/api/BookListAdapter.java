package it.jaschke.alexandria.api;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;

/**
 * Created by saj on 11/01/15.
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private Context context;

    private Cursor cursor;

    private Callback callback;

    public void initialize(Context context, Callback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item, viewGroup, false);
        return new ViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        if(cursor.moveToPosition(position))
        {
            String bookId = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID));
            String imgUrl = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            String title = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            String subtitle = cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            viewHolder.bind(bookId, imgUrl, title, subtitle, context);
        }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView bookCover;
        private final TextView bookTitle;
        private final TextView bookSubTitle;
        private final Callback callback;
        private String ean;

        public ViewHolder(View view, Callback callback) {
            super(view);
            bookCover = (ImageView) view.findViewById(R.id.fullBookCover);
            bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
            this.callback = callback;
            view.setOnClickListener(this);
        }

        public void bind(String bookId, String imgUrl, String title, String subtitle, Context context)
        {
            this.ean = bookId;
            Glide.with(context).load(imgUrl).placeholder(R.drawable.placeholder)
                    .into(bookCover);
            bookTitle.setText(title);
            bookSubTitle.setText(subtitle);
        }

        @Override
        public void onClick(View view)
        {
            callback.onItemSelected(ean, bookTitle.getText().toString());
        }
    }
}
