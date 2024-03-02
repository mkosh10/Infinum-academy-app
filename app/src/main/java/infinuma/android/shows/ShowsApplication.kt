package infinuma.android.shows

import android.app.Application
import infinuma.android.shows.ShowsDatabase.ShowEntity
import infinuma.android.shows.db.ShowsAppDatabase
import infinuma.android.shows.module.Show
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShowsApplication : Application() {
    val database by lazy {
        ShowsAppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}