package com.skillbox.ascent.data.activity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skillbox.ascent.data.db.models.DetailActivityContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = DetailActivityContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class DetailedActivity(
    @Json(name = "id")
    @PrimaryKey
    @ColumnInfo(name = DetailActivityContract.Columns.ID)
    var id: String,
    @Json(name = "name")
    @ColumnInfo(name = DetailActivityContract.Columns.NAME)
    val name: String,
    @Json(name = "type")
    @ColumnInfo(name = DetailActivityContract.Columns.TYPE)
    val type: String,
    @Json(name = "start_date_local")
    @ColumnInfo(name = DetailActivityContract.Columns.START_DAY_LOCAL)
    val startDateLocal: String,
    @Json(name = "elapsed_time")
    @ColumnInfo(name = DetailActivityContract.Columns.ELAPCED_TIME)
    val elapsedTime: Int,
    @Json(name = "description")
    @ColumnInfo(name = DetailActivityContract.Columns.DESCRIPTION)
    val description: String?,
    @Json(name = "distance")
    @ColumnInfo(name = DetailActivityContract.Columns.DISTANCE)
    val distance: Float,
    @Json(name = "trainer")
    @ColumnInfo(name = DetailActivityContract.Columns.TRAINER)
    val trainer: Boolean,
    @Json(name = "commute")
    @ColumnInfo(name = DetailActivityContract.Columns.COMMUTE)
    val commute: Boolean,
    @Json(name = "total_elevation_gain")
    @ColumnInfo(name = DetailActivityContract.Columns.ELEVATION_GAIN)
    val totalElevationGain: Float
)
