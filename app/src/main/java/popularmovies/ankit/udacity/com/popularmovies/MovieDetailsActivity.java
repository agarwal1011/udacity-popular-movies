package popularmovies.ankit.udacity.com.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import popularmovies.ankit.udacity.com.popularmovies.model.Movie;
import popularmovies.ankit.udacity.com.popularmovies.utils.NetworkUtil;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_INFO = "EXTRA_MOVIE_INFO";
    private Movie mMovieInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        if (getIntent() != null){
            mMovieInfo = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE_INFO);
            if (mMovieInfo != null) {
                setDetails();
            }
        }
    }

    private void setDetails() {
        //Title
        setTitle(mMovieInfo.getTitle());

        ((TextView) findViewById(R.id.movie_original_title_tv)).setText(mMovieInfo.getOriginalTitle());
        Picasso.with(this).load(NetworkUtil.getHighQualityImageUrl() +
                mMovieInfo.getPosterUrl()).into((ImageView) findViewById(R.id.movie_thumbnail_tv));
        ((TextView) findViewById(R.id.movie_user_rating_tv)).setText(String.valueOf(mMovieInfo.getRanking()));
        ((TextView) findViewById(R.id.movie_overview_tv)).setText(mMovieInfo.getOverview());
        ((TextView) findViewById(R.id.movie_release_date_tv)).setText(mMovieInfo.getReleaseDate());
    }
}
