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

    public static PictureRatingRequest forLikeReview(String pictureUrl) {
        return new PictureRatingRequest(pictureUrl, Rating.LIKE, null);
    }

    public static PictureRatingRequest forDislikeReview(String pictureUrl) {
        return new PictureRatingRequest(pictureUrl, Rating.DISLIKE, null);
    }

    public static PictureRatingRequest forFavoriteReview(String pictureUrl) {
        return new PictureRatingRequest(pictureUrl, Rating.FAVORITE, null);
    }

    public static PictureRatingRequest forCommentReview(String pictureUrl, String comments) {
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
