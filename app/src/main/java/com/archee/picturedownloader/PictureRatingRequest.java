package com.archee.picturedownloader;

public class PictureRatingRequest {

    private String pictureUrl;
    private Rating rating;
    private String comments;

    protected PictureRatingRequest(String pictureUrl, Rating rating, String comments) {
        this.pictureUrl = pictureUrl;
        this.rating = rating;
        this.comments = comments;
    }

    public static final PictureRatingRequest forLikeDislikeReview(String pictureUrl, Rating.LikeDislikeFavorite ratingValue) {
        return new PictureRatingRequest(pictureUrl, new Rating(ratingValue), null);
    }

    public static final PictureRatingRequest forStarsReview(String pictureUrl, Rating.Stars ratingValue) {
        return new PictureRatingRequest(pictureUrl, new Rating(ratingValue), null);
    }

    public static final PictureRatingRequest forCommentReview(String pictureUrl, String comments) {
        return new PictureRatingRequest(pictureUrl, null, comments);
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Rating getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }
}
