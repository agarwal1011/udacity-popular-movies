package popularmovies.ankit.udacity.com.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import popularmovies.ankit.udacity.com.popularmovies.database.AppDatabase;
import popularmovies.ankit.udacity.com.popularmovies.database.MovieEntry;
import popularmovies.ankit.udacity.com.popularmovies.model.Review;
import popularmovies.ankit.udacity.com.popularmovies.model.Trailer;
import popularmovies.ankit.udacity.com.popularmovies.utils.JsonUtil;
import popularmovies.ankit.udacity.com.popularmovies.utils.NetworkUtil;

import java.io.IOException;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_INFO = "EXTRA_MOVIE_INFO";
    private MovieEntry mMovieInfo;
    private RecyclerView mReviewsListView;
    private ProgressBar mReviewsProgressBar;
    private RecyclerView mTrailersListView;
    private ProgressBar mTrailersProgressBar;

    private AppDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        setTitle(R.string.movie_details_activity_title);

        mReviewsProgressBar = findViewById(R.id.reviews_progress_bar);
        mTrailersProgressBar = findViewById(R.id.trailers_progress_bar);

        if (getIntent() != null) {
            mMovieInfo = (MovieEntry) getIntent().getSerializableExtra(EXTRA_MOVIE_INFO);
            if (mMovieInfo != null) {
                setDetails();
                new FetchReviewData().execute(String.valueOf(mMovieInfo.getId()));
                new FetchTrailerData().execute(String.valueOf(mMovieInfo.getId()));
            }
        }
        mDatabase = AppDatabase.getInstance(getApplicationContext());
    }

    private void setDetails() {
        //Title
        ((TextView) findViewById(R.id.movie_title_tv)).setText(mMovieInfo.getTitle());
        Picasso.with(this)
                .load(NetworkUtil.getHighQualityImageUrl() +
                        mMovieInfo.getPosterUrl())
                .placeholder(R.mipmap.drawable_default_image)
                .error(R.mipmap.drawable_default_image)
                .into((ImageView) findViewById(R.id.movie_thumbnail_tv));
        ((TextView) findViewById(R.id.movie_user_rating_tv)).setText(getFormattedRating(mMovieInfo.getRanking()));
        ((TextView) findViewById(R.id.movie_overview_tv)).setText(mMovieInfo.getOverview());
        ((TextView) findViewById(R.id.movie_release_date_tv)).setText(getReleaseYear(mMovieInfo.getReleaseDate()));
        findViewById(R.id.mark_as_favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMarkAsFavoriteClicked(mMovieInfo);
            }
        });
        mReviewsListView = findViewById(R.id.reviews_list_view);
        mReviewsListView.setLayoutManager(new LinearLayoutManager(this));
        mTrailersListView = findViewById(R.id.trailers_list_view);
        mTrailersListView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onMarkAsFavoriteClicked(MovieEntry movieInfo) {
        final MovieEntry entry = new MovieEntry(movieInfo.getId(), movieInfo.getTitle(),
                movieInfo.getOriginalTitle(), movieInfo.getOverview(), movieInfo.getReleaseDate(), movieInfo.getPopularity(),
                movieInfo.getVoteCount(), movieInfo.getRanking(), movieInfo.getPosterUrl());
        addOrRemoveFromFavorites(entry);
    }

    private void addOrRemoveFromFavorites(final MovieEntry movie) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieEntry movieEntry = mDatabase.favouriteMovieDao().loadMovieById(movie.getId());
                if (movieEntry != null) {
                    mDatabase.favouriteMovieDao().deleteMovie(movieEntry);
                } else {
                    mDatabase.favouriteMovieDao().insertMovie(movie);
                }
            }
        });
    }

    private String getReleaseYear(String date) {
        if (TextUtils.isEmpty(date)) {
            return date;
        }
        return date.substring(0, 4);
    }

    private String getFormattedRating(double rating) {
        return rating + "/10";
    }

    private void updateReviewsVisibility(boolean loadingDone) {
        mReviewsProgressBar.setVisibility(loadingDone ? View.GONE : View.VISIBLE);
        mReviewsListView.setVisibility(loadingDone ? View.VISIBLE : View.GONE);
    }

    private void updateTrailersVisibility(boolean loadingDone) {
        mTrailersProgressBar.setVisibility(loadingDone ? View.GONE : View.VISIBLE);
        mReviewsListView.setVisibility(loadingDone ? View.VISIBLE : View.GONE);
    }


    private void showReviewErrorMessage() {
        Toast.makeText(this, getString(R.string.reviews_error_message), Toast.LENGTH_SHORT).show();
    }

    private void showTrailerErrorMessage() {
        Toast.makeText(this, getString(R.string.trailers_error_message), Toast.LENGTH_SHORT).show();
    }

    class FetchReviewData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            updateReviewsVisibility(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                response = NetworkUtil.getReviews(mMovieInfo.getId());
            } catch (IOException e) {
                showReviewErrorMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            updateReviewsVisibility(true);
            try {
                List<Review> reviewList = JsonUtil.getReviewsList(s);
                if (reviewList == null || reviewList.size() == 0) {
                    showReviewErrorMessage();
                    return;
                }
                ReviewAdapter adapter = new ReviewAdapter();
                adapter.setItems(reviewList);
                mReviewsListView.setAdapter(adapter);
            } catch (Exception e) {
                showReviewErrorMessage();
            }

        }
    }

    class FetchTrailerData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            updateTrailersVisibility(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                response = NetworkUtil.getTrailers(mMovieInfo.getId());
            } catch (IOException e) {
                showTrailerErrorMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            updateTrailersVisibility(true);
            try {
                List<Trailer> trailerList = JsonUtil.getTrailersList(s);
                if (trailerList == null || trailerList.size() == 0) {
                    showTrailerErrorMessage();
                    return;
                }
                TrailerAdapter adapter = new TrailerAdapter();
                adapter.setItems(MovieDetailsActivity.this, trailerList);
                mTrailersListView.setAdapter(adapter);
            } catch (Exception e) {
                showTrailerErrorMessage();
            }

        }
    }
}
