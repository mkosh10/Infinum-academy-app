package infinuma.android.shows.ui.shows

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ViewShowsItemBinding
import infinuma.android.shows.module.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val onItemClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowsViewHolder>() {
    inner class ShowsViewHolder(private val cardShowViewBind: ShowsCardView) : RecyclerView.ViewHolder(cardShowViewBind) {
        fun bind(show: Show) {
            cardShowViewBind.getCardContainer().setOnClickListener{
                onItemClickCallback.invoke(show)
            }
            show.showImageUrl?.let { cardShowViewBind.setShowPhoto(it) }
            show.title?.let { cardShowViewBind.setTitle(it) }
            show.description?.let { cardShowViewBind.setShowsDesc(it) }

            if(show.id.toInt() % 2 == 0){
                cardShowViewBind.animCardContainerOdd()
            } else {
                cardShowViewBind.animCardContainerEven()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowsViewHolder {
        val showsCardView = ShowsCardView(parent.context)
        return ShowsViewHolder(showsCardView)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ShowsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setList(showsListData: List<Show>) {
        items = showsListData
        notifyDataSetChanged()
    }


}