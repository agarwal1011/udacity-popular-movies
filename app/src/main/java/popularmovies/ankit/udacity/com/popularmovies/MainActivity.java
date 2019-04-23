package popularmovies.ankit.udacity.com.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import popularmovies.ankit.udacity.com.popularmovies.database.MovieEntry;
import popularmovies.ankit.udacity.com.popularmovies.utils.JsonUtil;
import popularmovies.ankit.udacity.com.popularmovies.utils.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MOVIES_GRID_VIEW_POSITION = "MOVIES_GRID_VIEW_POSITION";
    private static final String IS_MOST_POPULAR_LISTING_SHOWN = "IS_MOST_POPULAR_LISTING_SHOWN";
    private static final String IS_FAVORITE_LISTING_SHOWN = "IS_FAVORITE_LISTING_SHOWN";

    private List<MovieEntry> mMovieList = new ArrayList<>();
    private List<MovieEntry> mFavoriteMovies = new ArrayList<>();
    private MoviePosterAdapter mMoviePosterAdapter;
    private ProgressBar mProgressBar;
    private GridView mGridView;
    private int mGridViewPosition;
    private boolean mIsMostPopularListingShown;
    private boolean mIsFavoriteMoviesListingShown;
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
                MovieEntry movieEntry = (MovieEntry) adapterView.getItemAtPosition(i);
                if (movieEntry == null) {
                    showErrorDialog();
                    return;
                }
                launchMovieDetailsPage(movieEntry);
            }
        });

        setupViewModel();

        if (savedInstanceState == null) {
            loadMoviePosters(true);
        } else {
            mGridViewPosition = savedInstanceState.getInt(MOVIES_GRID_VIEW_POSITION);
            mIsFavoriteMoviesListingShown = savedInstanceState.getBoolean(IS_FAVORITE_LISTING_SHOWN, false);
            if (mIsFavoriteMoviesListingShown) {
                setMovieAdapter(mFavoriteMovies);
            } else {
                mIsMostPopularListingShown = savedInstanceState.getBoolean(IS_MOST_POPULAR_LISTING_SHOWN, true);
                loadMoviePosters(mIsMostPopularListingShown);
            }
        }
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getListOfFavoriteMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> favouriteMovieEntries) {
                mFavoriteMovies = favouriteMovieEntries;
                if (mIsFavoriteMoviesListingShown) {
                    setMovieAdapter(mFavoriteMovies);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_poster, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mGridViewPosition = 0;
        mIsFavoriteMoviesListingShown = false;
        switch (item.getItemId()) {
            case R.id.movie_poster_sort_by_popularity:
                loadMoviePosters(true);
                return true;
            case R.id.movie_poster_sort_by_top_rated:
                loadMoviePosters(false);
                return true;
            case R.id.movie_poster_favorites:
                mIsFavoriteMoviesListingShown = true;
                setMovieAdapter(mFavoriteMovies);
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
        outState.putBoolean(IS_FAVORITE_LISTING_SHOWN, mIsFavoriteMoviesListingShown);
        super.onSaveInstanceState(outState);
    }

    private void launchMovieDetailsPage(MovieEntry movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_INFO, movie);
        startActivity(intent);
    }

    private void loadMoviePosters(boolean showPopularMovies) {
        if (showPopularMovies) {
            mCurrentMovieUrl = NetworkUtil.POPULAR_MOVIES_URL;
            new FetchMoviePosterData().execute(mCurrentMovieUrl);
        } else {
            mCurrentMovieUrl = NetworkUtil.TOP_RATED_MOVIES_URL;
            new FetchMoviePosterData().execute(mCurrentMovieUrl);
        }
        mIsMostPopularListingShown = showPopularMovies;
    }

    private void setMovieAdapter(List<MovieEntry> movies) {
        mMoviePosterAdapter = new MoviePosterAdapter(MainActivity.this);
        mMoviePosterAdapter.setData(movies);
        mGridView.setAdapter(mMoviePosterAdapter);
        mGridView.smoothScrollToPosition(mGridViewPosition);
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
                mMovieList = JsonUtil.getMovieList(s);
                if (mMovieList == null || mMovieList.size() == 0) {
                    showErrorDialog();
                    return;
                }
                setMovieAdapter(mMovieList);
            } catch (JSONException e) {
                showErrorDialog();
            }

        }
    }
}
