package popularmovies.ankit.udacity.com.popularmovies.utils;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import popularmovies.ankit.udacity.com.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieJsonUtil {

    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String THUMBNAIL_LINK = "poster_path";
    private static final String VOTE_COUNT = "vote_count";
    private static final String POPULARITY = "popularity";
    private static final String RATING = "vote_average";


    public static List<Movie> getMovieList(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        List<Movie> movies = new ArrayList<>();

        JSONObject movieListObject = new JSONObject(jsonString);

        JSONArray listOfMovies = movieListObject.getJSONArray(RESULTS);

        if (listOfMovies != null) {
            for (int i = 0; i < listOfMovies.length(); i++) {
                JSONObject movieObject = listOfMovies.getJSONObject(i);
                if (movieObject != null) {
                    Movie movie = new Movie();
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
}