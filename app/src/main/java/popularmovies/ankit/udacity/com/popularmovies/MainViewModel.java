package popularmovies.ankit.udacity.com.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import popularmovies.ankit.udacity.com.popularmovies.database.AppDatabase;
import popularmovies.ankit.udacity.com.popularmovies.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> mListOfFavoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mListOfFavoriteMovies = appDatabase.favouriteMovieDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getListOfFavoriteMovies() {
        return mListOfFavoriteMovies;
    }
}
