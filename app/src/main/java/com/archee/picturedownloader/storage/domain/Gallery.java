package com.archee.picturedownloader.storage.domain;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Gallery implements Parcelable {
    private String title;
    private String description;
    private List<Bitmap> pictures;

    public Gallery(String title, String description, List<Bitmap> pictures) {
        this.title = title;
        this.description = description;
        this.pictures = pictures;
    }

    public List<Bitmap> getPictures() {
        return pictures;
    }

    public void setPictures(List<Bitmap> pictures) {
        this.pictures = pictures;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeTypedList(pictures);
    }

    private Gallery(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        in.readTypedList(pictures, Bitmap.CREATOR);
    }

    public static final Parcelable.Creator<Gallery> CREATOR = new Parcelable.Creator<Gallery>() {
        public Gallery createFromParcel(Parcel source) {
            return new Gallery(source);
        }

        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };
}
