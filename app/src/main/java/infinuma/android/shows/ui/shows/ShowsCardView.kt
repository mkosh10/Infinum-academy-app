package infinuma.android.shows.ui.shows

import android.content.Context
import android.graphics.Color.alpha
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ViewShowsItemBinding

class ShowsCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FrameLayout(context, attrs, defStyleAttrs) {
    private val binding: ViewShowsItemBinding

    init {
        binding = ViewShowsItemBinding.inflate(LayoutInflater.from(context), this)
        clipToPadding = false

    }

    fun getCardContainer(): CardView {
        return binding.cardContainer
    }

    fun setTitle(title: String) {
        binding.showName.text = title
    }

    fun setShowPhoto(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.loading_image)
            .into(binding.showImage)
    }

    fun setShowsDesc(desc: String) {
        binding.showText.text = desc
    }

    fun animCardContainerOdd() = with(binding.cardContainer){
        translationX = -1000f
        animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(500)
            .start()
    }
    fun animCardContainerEven() = with(binding.cardContainer) {
        translationX = 1000f
        animate()
            .translationX(0f)
            .alpha(1f)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(500)
            .start()
    }


}