package com.skillbox.ascent.data.activity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skillbox.ascent.data.db.models.ActivityContract

@Entity(tableName = ActivityContract.TABLE_NAME)
data class Activity(
    @ColumnInfo(name = ActivityContract.Columns.NAME)
    val name: String,
    @ColumnInfo(name = ActivityContract.Columns.TYPE)
    val type: String,
    @PrimaryKey
    @ColumnInfo(name = ActivityContract.Columns.START_DAY_LOCAL)
    val startDateLocal: String,
    @ColumnInfo(name = ActivityContract.Columns.ELAPCED_TIME)
    val elapsedTime: Int,
    @ColumnInfo(name = ActivityContract.Columns.DESCRIPTION)
    val description: String,
    @ColumnInfo(name = ActivityContract.Columns.DISTANCE)
    val distance: Float,
    @ColumnInfo(name = ActivityContract.Columns.TRAINER)
    val trainer: Int,
    @ColumnInfo(name = ActivityContract.Columns.COMMUTE)
    val commute: Int
)
//$ http POST "https://www.strava.com/api/v3/activities" title='value' type='value' start_date_local='value' elapsed_time='value' description='value' distance='value' trainer='value' commute='value' "Authorization: Bearer [[token]]