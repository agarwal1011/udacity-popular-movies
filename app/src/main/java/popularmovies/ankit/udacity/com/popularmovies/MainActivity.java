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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Movie> mMovieList = new ArrayList<>();
    private MoviePosterAdapter mMoviePosterAdapter;
    private ProgressBar mProgressBar;
    private GridView mGridView;

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
        loadMoviePosters();
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
                Collections.sort(mMovieList, getMoviesSortedByPopularity());
                mMoviePosterAdapter.setData(mMovieList);
                return true;
            case R.id.movie_poster_sort_by_top_rated:
                Collections.sort(mMovieList, getMoviesSortedByRanking());
                mMoviePosterAdapter.setData(mMovieList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchMovieDetailsPage(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_INFO, movie);
        startActivity(intent);
    }

    private Comparator<Movie> getMoviesSortedByPopularity() {
        return new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                if (movie1.getPopularity() > movie2.getPopularity()) {
                    return -1;
                }
                return 1;
            }
        };
    }

    private Comparator<Movie> getMoviesSortedByRanking() {
        return new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                if (movie1.getRanking() > movie2.getRanking()) {
                    return -1;
                }
                return 1;
            }
        };
    }

    private void loadMoviePosters() {
        new FetchMoviePosterData().execute();
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
                response = NetworkUtil.getMovieDetails();
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
            } catch (JSONException e) {
                showErrorDialog();
            }

        }
    }
}
