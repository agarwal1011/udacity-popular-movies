package popularmovies.ankit.udacity.com.popularmovies.utils;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import popularmovies.ankit.udacity.com.popularmovies.database.MovieEntry;
import popularmovies.ankit.udacity.com.popularmovies.model.Movie;
import popularmovies.ankit.udacity.com.popularmovies.model.Review;
import popularmovies.ankit.udacity.com.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static final String RESULTS = "results";

    //Movie params
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String THUMBNAIL_LINK = "poster_path";
    private static final String VOTE_COUNT = "vote_count";
    private static final String POPULARITY = "popularity";
    private static final String RATING = "vote_average";
    private static final String NAME = "name";
    private static final String KEY  = "key";

    //Review params
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";

    public static List<MovieEntry> getMovieList(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        List<MovieEntry> movies = new ArrayList<>();

        JSONObject movieListObject = new JSONObject(jsonString);

        JSONArray listOfMovies = movieListObject.getJSONArray(RESULTS);

        if (listOfMovies != null) {
            for (int i = 0; i < listOfMovies.length(); i++) {
                JSONObject movieObject = listOfMovies.getJSONObject(i);
                if (movieObject != null) {
                    MovieEntry movie = new MovieEntry();
                    movie.setId(movieObject.getLong(ID));
                    movie.setPopularity(movieObject.getDouble(POPULARITY));
                    movie.setPosterUrl(movieObject.getString(THUMBNAIL_LINK));
                    movie.setRanking(movieObject.getDouble(RATING));
                    movie.setTitle(movieObject.getString(TITLE));
                    movie.setOriginalTitle(movieObject.getString(ORIGINAL_TITLE));
                    movie.setOverview(movieObject.getString(OVERVIEW));
                    movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
                    movie.setVoteCount(movieObject.getLong(VOTE_COUNT));
                    movies.add(movie);
                }
            }
        }
        return movies;
    }

    public static List<Review> getReviewsList(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        List<Review> reviews = new ArrayList<>();

        JSONObject reviewListObject = new JSONObject(jsonString);

        JSONArray listOfReviews = reviewListObject.getJSONArray(RESULTS);

        if (listOfReviews != null) {
            for (int i = 0; i < listOfReviews.length(); i++) {
                JSONObject reviewObject = listOfReviews.getJSONObject(i);
                if (reviewObject != null) {
                    Review review = new Review();
                    review.setAuthor(reviewObject.getString(AUTHOR));
                    review.setContent(reviewObject.getString(CONTENT));
                    reviews.add(review);
                }
            }
        }
        return reviews;
    }

    public static List<Trailer> getTrailersList(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        List<Trailer> trailers = new ArrayList<>();

        JSONObject trailerListObject = new JSONObject(jsonString);

        JSONArray listOfTrailers = trailerListObject.getJSONArray(RESULTS);

        if (listOfTrailers != null) {
            for (int i = 0; i < listOfTrailers.length(); i++) {
                JSONObject trailerObject = listOfTrailers.getJSONObject(i);
                if (trailerObject != null) {
                    Trailer trailer = new Trailer();
                    trailer.setTitle(trailerObject.getString(NAME));
                    trailer.setTrailerId(trailerObject.getString(KEY));
                    trailers.add(trailer);
                }
            }
        }
        return trailers;
    }
}