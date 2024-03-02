package infinuma.android.shows.ui.show_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ViewReviewsItemBinding
import infinuma.android.shows.module.Review

class ReviewsAdapter(
    private var items: List<Review>,
    private val onItemClickCallback: (Review) -> Unit
) : RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {
    inner class ReviewsViewHolder(private val binding: ViewReviewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            with(binding) {
                cardContainer.setOnClickListener {
                    onItemClickCallback.invoke(review)
                }

                Glide.with(this.root)
                    .load(review.user.imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(profilePictureReview)
                nicknameContainer.text = review.user.email.split("@")[0]
                rateContainer.text = review.rating.toString()
                commentContainer.text = review.comment
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val binding = ViewReviewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewsViewHolder(binding)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addReview(newReview: Review) {
        items += newReview
        notifyDataSetChanged()
    }

    fun newItemList(updatedList: List<Review>) {
        items = updatedList
        notifyDataSetChanged()
    }


}