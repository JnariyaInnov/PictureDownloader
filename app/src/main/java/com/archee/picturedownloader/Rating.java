package com.archee.picturedownloader;

public class Rating {

    private RatingType ratingType;
    private LikeDislikeFavorite likeDislikeFavoriteValue;
    private Stars starValue;

    public enum RatingType {LIKE_DISLIKE_FAVORITE, STARS}
    
    public enum LikeDislikeFavorite {
        LIKE(1), DISLIKE(2), FAVORITE(3);
        
        private final int value;

        LikeDislikeFavorite(int value) {
            this.value = value;
        }
    }

    public enum Stars {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

        private final int value;

        Stars(int value) {
            this.value = value;
        }
    }

    public Rating(LikeDislikeFavorite ratingValue) {
        ratingType = RatingType.LIKE_DISLIKE_FAVORITE;
        likeDislikeFavoriteValue = ratingValue;
    }

    public Rating(Stars ratingValue) {
        ratingType = RatingType.STARS;
        starValue = ratingValue;
    }
}
