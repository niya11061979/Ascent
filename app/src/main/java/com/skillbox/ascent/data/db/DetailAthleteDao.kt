package com.skillbox.ascent.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillbox.ascent.data.DetailedAthlete
import com.skillbox.ascent.data.db.models.DetailAthleteContract

@Dao
interface DetailAthleteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: DetailedAthlete)

    @Query("SELECT * FROM ${DetailAthleteContract.TABLE_NAME}")
    suspend fun getAthlete(): DetailedAthlete


}