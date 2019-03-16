package popularmovies.ankit.udacity.com.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import org.json.JSONException;
import popularmovies.ankit.udacity.com.popularmovies.model.Movie;
import popularmovies.ankit.udacity.com.popularmovies.utils.MovieJsonUtil;
import popularmovies.ankit.udacity.com.popularmovies.utils.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MOVIES_GRID_VIEW_POSITION = "MOVIES_GRID_VIEW_POSITION";
    private static final String IS_MOST_POPULAR_LISTING_SHOWN = "IS_MOST_POPULAR_LISTING_SHOWN";

    private List<Movie> mMovieList = new ArrayList<>();
    private MoviePosterAdapter mMoviePosterAdapter;
    private ProgressBar mProgressBar;
    private GridView mGridView;
    private int mGridViewPosition;
    private boolean mIsMostPopularListingShown;
    private String mCurrentMovieUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progress_bar);
        mGridView = findViewById(R.id.movies_grid);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mMovieList == null || mMovieList.get(i) == null) {
                    showErrorDialog();
                    return;
                }
                launchMovieDetailsPage(mMovieList.get(i));
            }
        });
        if (savedInstanceState == null) {
            loadMoviePosters(true);
        } else {
            mGridViewPosition = savedInstanceState.getInt(MOVIES_GRID_VIEW_POSITION);
            mIsMostPopularListingShown = savedInstanceState.getBoolean(IS_MOST_POPULAR_LISTING_SHOWN, true);
            loadMoviePosters(savedInstanceState.getBoolean(IS_MOST_POPULAR_LISTING_SHOWN, true));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_poster, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movie_poster_sort_by_popularity:
                mGridViewPosition = 0;
                loadMoviePosters(true);
                return true;
            case R.id.movie_poster_sort_by_top_rated:
                mGridViewPosition = 0;
                loadMoviePosters(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int index = mGridView != null ? mGridView.getFirstVisiblePosition() : 0;
        outState.putInt(MOVIES_GRID_VIEW_POSITION, index);
        outState.putBoolean(IS_MOST_POPULAR_LISTING_SHOWN, mIsMostPopularListingShown);
        super.onSaveInstanceState(outState);
    }

    private void launchMovieDetailsPage(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_INFO, movie);
        startActivity(intent);
    }

    private void loadMoviePosters(boolean showPopularMovies) {
        if (showPopularMovies && !NetworkUtil.POPULAR_MOVIES_URL.equalsIgnoreCase(mCurrentMovieUrl)) {
            mCurrentMovieUrl = NetworkUtil.POPULAR_MOVIES_URL;
            new FetchMoviePosterData().execute(mCurrentMovieUrl);
        } else if (!showPopularMovies && !NetworkUtil.TOP_RATED_MOVIES_URL.equalsIgnoreCase(mCurrentMovieUrl)) {
            mCurrentMovieUrl = NetworkUtil.TOP_RATED_MOVIES_URL;
            new FetchMoviePosterData().execute(mCurrentMovieUrl);
        }
        mIsMostPopularListingShown = showPopularMovies;
    }

    private void updateVisibility(boolean loadingDone) {
        mProgressBar.setVisibility(loadingDone ? View.GONE : View.VISIBLE);
        mGridView.setVisibility(loadingDone ? View.VISIBLE : View.GONE);
    }

    private void showErrorDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.error_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    class FetchMoviePosterData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            updateVisibility(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                response = NetworkUtil.getMoviesList(strings[0]);
            } catch (IOException e) {
                showErrorDialog();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            updateVisibility(true);
            try {
                mMovieList = MovieJsonUtil.getMovieList(s);
                if (mMovieList == null || mMovieList.size() == 0) {
                    showErrorDialog();
                    return;
                }
                mMoviePosterAdapter = new MoviePosterAdapter(MainActivity.this);
                mMoviePosterAdapter.setData(mMovieList);
                mGridView.setAdapter(mMoviePosterAdapter);
                mGridView.smoothScrollToPosition(mGridViewPosition);
            } catch (JSONException e) {
                showErrorDialog();
            }

        }
    }
}
