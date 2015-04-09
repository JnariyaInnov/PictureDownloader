package com.archee.picturedownloader;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PictureRatingService extends IntentService {

    private static final String TAG = PictureRatingService.class.getSimpleName();

    private static final String RATING = "rating";
    private static final String RATING_ITEM = "item";
    private static final String RATING_LIKE = "like";
    private static final String RATING_DISLIKE = "dislike";
    private static final String RATING_FAVORITE = "favorite";
    private static final String RATING_COMMENT = "comment";

    private PictureRatingRESTService mService;

    public PictureRatingService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://shrouded-thicket-7737.herokuapp.com")
                .build();

        mService = restAdapter.create(PictureRatingRESTService.class);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (intent != null) {
            String rating = intent.getStringExtra(RATING);
            String url = intent.getStringExtra(RATING_ITEM);

            Callback<Response> cb = new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Log.i(TAG, "Success! Response code: " + response.getStatus());
                    Log.i(TAG, response.getBody().toString());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Fail! " + error);
                }
            };

            // Would like to use switch statement here but my IntelliJ + MacOS are having issues with using JDK 7 and won't let me compile a string switch...
            if (rating.equals(RATING_LIKE)) {
                mService.createPictureReview(PictureRatingRequest.forLikeReview(url), cb);
            } else if (rating.equals(RATING_DISLIKE)) {
                mService.createPictureReview(PictureRatingRequest.forDislikeReview(url), cb);
            } else if (rating.equals(RATING_FAVORITE)) {
                mService.createPictureReview(PictureRatingRequest.forFavoriteReview(url), cb);
            } else if (rating.equals(RATING_COMMENT)) {
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                mService.createPictureReview(PictureRatingRequest.forCommentReview(url, remoteInput.getCharSequence(RATING_COMMENT).toString()), cb);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
