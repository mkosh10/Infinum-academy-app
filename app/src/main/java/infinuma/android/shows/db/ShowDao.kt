package infinuma.android.shows.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import infinuma.android.shows.ShowsDatabase.ShowEntity
import infinuma.android.shows.module.Show

@Dao
interface ShowDao {

    @Query("SELECT * FROM show")
    fun getAllShows(): LiveData<List<ShowEntity>>

    @Query("SELECT * FROM show WHERE id IS :showId")
    suspend fun getShow(showId: String): ShowEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllShows(shows: List<ShowEntity>)

    //for testing
    @Query("DELETE FROM show")
    suspend fun deleteAll()
}