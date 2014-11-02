package com.archee.picturedownloader.async;

import android.graphics.Bitmap;

import java.net.URL;

public class ImageResponse {

    private URL url;
    private Bitmap image;
    private int responseCode;

    public ImageResponse(URL url, Bitmap image, int responseCode) {
        this.url = url;
        this.image = image;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
