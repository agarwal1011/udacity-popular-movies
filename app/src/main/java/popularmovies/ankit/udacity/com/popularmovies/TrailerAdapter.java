package popularmovies.ankit.udacity.com.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import popularmovies.ankit.udacity.com.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerVH> {

    private Activity mActivity;
    private List<Trailer> trailers = new ArrayList<>();

    public void setItems(Activity activity, List<Trailer> items) {
        mActivity = activity;
        trailers.addAll(items);
    }

    private void watchTrailerOnYouTube(String id) {
        mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id)));
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerVH trailerVH, int i) {
        final Trailer trailer = trailers.get(trailerVH.getAdapterPosition());
        if (trailer != null) {
            trailerVH.trailerName.setText(trailer.getTitle());
            trailerVH.playTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchTrailerOnYouTube(trailer.getTrailerId());
                    Toast.makeText(mActivity, trailer.getTrailerId(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NonNull
    @Override
    public TrailerVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_view, viewGroup, false);
        return new TrailerVH(view);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class TrailerVH extends RecyclerView.ViewHolder {

        ImageView playTrailer;
        TextView trailerName;

        public TrailerVH(@NonNull View itemView) {
            super(itemView);
            playTrailer = itemView.findViewById(R.id.play_trailer);
            trailerName = itemView.findViewById(R.id.trailer_name);
        }
    }
}
