package popularmovies.ankit.udacity.com.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import popularmovies.ankit.udacity.com.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {

    private List<Review> reviews = new ArrayList<>();

    public void setItems(List<Review> items) {
        reviews.addAll(items);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewVH reviewVH, int i) {
        Review review = reviews.get(reviewVH.getAdapterPosition());
        if (review != null) {
            reviewVH.author.setText(review.getAuthor());
            reviewVH.content.setText(review.getContent());
        }
    }

    @NonNull
    @Override
    public ReviewVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_layout, viewGroup, false);
        return new ReviewVH(view);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewVH extends RecyclerView.ViewHolder {

        TextView author;
        TextView content;

        public ReviewVH(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.review_author);
            content = itemView.findViewById(R.id.review_content);
        }
    }
}
