package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Julio Mendoza on 8/14/15.
 */
public class Book implements Parcelable {

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    /*
    final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String DESC = "description";
        final String CATEGORIES = "categories";
        final String IMG_URL_PATH = "imageLinks";
        final String IMG_URL = "thumbnail";
     */


    private String title;
    private String subtitle;
    private String description;
    private String authors;
    private String thumbnail;
    private String categories;

    public Book(String title, String subtitle, String description, String authors, String thumbnail,
                String categories) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.categories = categories;
    }

    protected Book(Parcel in) {
        title = in.readString();
        subtitle = in.readString();
        description = in.readString();
        authors = in.readString();
        thumbnail = in.readString();
        categories = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(description);
        dest.writeString(authors);
        dest.writeString(thumbnail);
        dest.writeString(categories);
    }
}
