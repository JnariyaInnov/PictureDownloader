package com.archee.picturedownloader;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface PictureRatingRESTService {

    @POST("/reviews")
    void createPictureReview(@Body PictureRatingRequest request, Callback<Response> cb);
}
