package popularmovies.ankit.udacity.com.popularmovies.model;

import java.io.Serializable;

public class Trailer implements Serializable {

    private String title;
    private String trailerId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }
}
