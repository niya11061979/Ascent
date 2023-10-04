package com.skillbox.ascent.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skillbox.ascent.data.db.models.DetailAthleteContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = DetailAthleteContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class DetailedAthlete(
    @Json(name = "id")
    @PrimaryKey
    @ColumnInfo(name = DetailAthleteContract.Columns.ID)
    val id: Int,
    @Json(name = "weight")
    @ColumnInfo(name = DetailAthleteContract.Columns.WEIGHT)
    val weight: Int,
    @Json(name = "friend_count")
    @ColumnInfo(name = DetailAthleteContract.Columns.FRIEND_COUNT)
    val countFollowing: Int,
    @Json(name = "follower_count")
    @ColumnInfo(name = DetailAthleteContract.Columns.FOLLOWER_COUNT)
    val countFollower: Int,
    @Json(name = "sex")
    @ColumnInfo(name = DetailAthleteContract.Columns.SEX)
    val gender: String?,
    @Json(name = "country")
    @ColumnInfo(name = DetailAthleteContract.Columns.COUNTRY)
    val country: String?,
    @Json(name = "profile")
    @ColumnInfo(name = DetailAthleteContract.Columns.AVATAR_URL)
    val avatarUrl: String,
    @Json(name = "username")
    @ColumnInfo(name = DetailAthleteContract.Columns.USER_NAME)
    val username: String?,
    @Json(name = "lastname")
    @ColumnInfo(name = DetailAthleteContract.Columns.LAST_NAME)
    val lastName: String,
    @Json(name = "firstname")
    @ColumnInfo(name = DetailAthleteContract.Columns.FIRST_NAME)
    val firstName: String
)
