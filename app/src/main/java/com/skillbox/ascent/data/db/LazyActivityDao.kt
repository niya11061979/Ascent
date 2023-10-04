package com.skillbox.ascent.data.db

import androidx.room.*
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.db.models.ActivityContract

@Dao
interface LazyActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLazyActivity(activity: Activity)

    @Transaction
    @Query("SELECT * FROM ${ActivityContract.TABLE_NAME}")
    suspend fun getLazyActivities(): List<Activity>

    @Query("DELETE  FROM ${ActivityContract.TABLE_NAME} WHERE ${ActivityContract.Columns.START_DAY_LOCAL}=:startDayLocal")
    suspend fun removeLazyActivity(startDayLocal: String)

}