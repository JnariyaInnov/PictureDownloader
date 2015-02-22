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

    // Binder given to clients
    /*private final IBinder mBinder = new RatingServiceBinder();*/

    public PictureRatingService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://192.168.1.115:3000")
                .build();

        mService = restAdapter.create(PictureRatingRESTService.class);
    }

    /*@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }*/

    @Override
    public void onHandleIntent(Intent intent) {
        if (intent != null) {
            String rating = intent.getStringExtra(RATING);
            String url = intent.getStringExtra(RATING_ITEM);

            Callback<Response> cb = new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Log.i(TAG, "Success! " + response);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i(TAG, "Fail! " + error);
                }
            };

            // Would like to use switch statement here but my IntelliJ + MacOS are having issues with using JDK 7 and won't let me compile a string switch...
            if (rating.equals(RATING_LIKE)) {
                mService.createPictureReview(PictureRatingRequest.forLikeDislikeReview(url, Rating.LikeDislikeFavorite.LIKE), cb);
            } else if (rating.equals(RATING_DISLIKE)) {
                mService.createPictureReview(PictureRatingRequest.forLikeDislikeReview(url, Rating.LikeDislikeFavorite.DISLIKE), cb);
            } else if (rating.equals(RATING_FAVORITE)) {
                mService.createPictureReview(PictureRatingRequest.forLikeDislikeReview(url, Rating.LikeDislikeFavorite.FAVORITE), cb);
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

    /*public class RatingServiceBinder extends Binder {
        PictureRatingService getService() {
            // Return this instance of PictureRatingService so clients can call public methods
            return PictureRatingService.this;
        }
    }*/
}
