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
    private String title;
    private String subtitle;
    private String desc;
    private String authors;
    private String imgUrl;
    private String categories;

    public Book(String title, String subtitle, String desc, String authors, String imgUrl, String categories) {
        this.title = title;
        this.subtitle = subtitle;
        this.desc = desc;
        this.authors = authors;
        this.imgUrl = imgUrl;
        this.categories = categories;
    }

    protected Book(Parcel in) {
        title = in.readString();
        subtitle = in.readString();
        desc = in.readString();
        authors = in.readString();
        imgUrl = in.readString();
        categories = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDesc() {
        return desc;
    }

    public String getAuthors() {
        return authors;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCategories() {
        return categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(desc);
        dest.writeString(authors);
        dest.writeString(imgUrl);
        dest.writeString(categories);
    }
}
