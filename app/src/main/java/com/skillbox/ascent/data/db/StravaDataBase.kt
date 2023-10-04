package com.skillbox.ascent.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skillbox.ascent.data.DetailedAthlete
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.activity.DetailedActivity

@Database(
    entities = [
        DetailedActivity::class,
        DetailedAthlete::class,
        Activity::class,
    ], version = StravaDataBase.DB_VERSION, exportSchema = false
)
abstract class StravaDataBase : RoomDatabase() {
    abstract fun detailActivityDao(): DetailActivityDao
    abstract fun detailAthleteDao(): DetailAthleteDao
    abstract fun lazyActivityDao(): LazyActivityDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "strava-database"
    }
}