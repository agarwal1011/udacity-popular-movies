package popularmovies.ankit.udacity.com.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import popularmovies.ankit.udacity.com.popularmovies.model.Movie;
import popularmovies.ankit.udacity.com.popularmovies.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class MoviePosterAdapter extends ArrayAdapter<Movie> {

    private final List<Movie> mMoviesList = new ArrayList<>();

    public MoviePosterAdapter(Context context) {
        super(context, 0);
    }

    public void setData(List<Movie> objects) {
        mMoviesList.clear();
        mMoviesList.addAll(objects);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView moviePoster = convertView.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load(NetworkUtil.getAverageQualityImageUrl() +
                movie.getPosterUrl()).into(moviePoster);

        return convertView;
    }

    @Override
    public int getCount() {
        return mMoviesList.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMoviesList.get(position);
    }
}
