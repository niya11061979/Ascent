package com.skillbox.ascent.data.db

import androidx.room.*
import com.skillbox.ascent.data.activity.DetailedActivity
import com.skillbox.ascent.data.db.models.DetailActivityContract

@Dao
interface DetailActivityDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<DetailedActivity>)

    @Transaction
    @Query("SELECT * FROM ${DetailActivityContract.TABLE_NAME}")
    suspend fun getAllActivities(): List<DetailedActivity>

    @Transaction
    @Query("DELETE FROM ${DetailActivityContract.TABLE_NAME}")
    suspend fun deleteAllActivities()

}
