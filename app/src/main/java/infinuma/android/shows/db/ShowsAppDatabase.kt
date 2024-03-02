package infinuma.android.shows.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import infinuma.android.shows.ShowsDatabase.ReviewEntity
import infinuma.android.shows.ShowsDatabase.ShowEntity
import infinuma.android.shows.module.Show

@Database(
    entities = [
        ShowEntity::class,
        ReviewEntity::class,
    ],
    version = 1
)
abstract class ShowsAppDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: ShowsAppDatabase? = null

        fun getDatabase(context: Context): ShowsAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context = context,
                    klass = ShowsAppDatabase::class.java,
                    name = "showsApp_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                database
            }
        }
    }

    abstract fun showDao(): ShowDao
    abstract fun reviewDao(): ReviewDao
}
