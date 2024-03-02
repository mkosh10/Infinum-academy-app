package infinuma.android.shows.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import infinuma.android.shows.ui.shows.ShowsViewModel
import infinuma.android.shows.ui.show_details.ShowDetailsViewModel

class ShowViewModelFactory(private val database: ShowsAppDatabase) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ShowsViewModel::class.java)) {
            return ShowsViewModel(database) as T

        } else if (modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)) {
            return ShowDetailsViewModel(database) as T
        }
        throw IllegalStateException("Sorry, can't work with non ShowsViewModel class")
    }
}