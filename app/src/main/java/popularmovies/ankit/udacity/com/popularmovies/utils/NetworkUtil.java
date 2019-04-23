package popularmovies.ankit.udacity.com.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtil {

    //TODO: Set your personal API key here
    private static final String API_KEY = "237b2345a17055572b7ce33ee93e22e4";
    public static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    public static final String TOP_RATED_MOVIES_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String FETCH_REVIEWS_ENDPOINT = "reviews";
    public static final String FETCH_TRAILERS_ENDPOINT = "videos";
    public static final String QUERY_PARAM_API = "api_key";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String IMAGE_QUALITY_AVERAGE = "/w185";
    private static final String IMAGE_QUALITY_HIGH = "/w500";

    public static String getAverageQualityImageUrl() {
        return IMAGE_BASE_URL + IMAGE_QUALITY_AVERAGE;
    }

    public static String getHighQualityImageUrl() {
        return IMAGE_BASE_URL + IMAGE_QUALITY_HIGH;
    }

    public static String getMoviesList(String moviesUrl) throws IOException {
        Uri builtUri = Uri.parse(moviesUrl).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return getResponseFromHttpUrl(url);
    }

    public static String getReviews(long id) throws IOException {
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(FETCH_REVIEWS_ENDPOINT)
                .appendQueryParameter(QUERY_PARAM_API, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return getResponseFromHttpUrl(url);
    }

    public static String getTrailers(long id) throws IOException {
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(FETCH_TRAILERS_ENDPOINT)
                .appendQueryParameter(QUERY_PARAM_API, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return getResponseFromHttpUrl(url);
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url
     *         The URL to fetch the HTTP response from.
     *
     * @return The contents of the HTTP response.
     *
     * @throws IOException
     *         Related to network and stream reading
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        if (url == null) {
            return null;
        }
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}