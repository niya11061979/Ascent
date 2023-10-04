package com.skillbox.ascent.networking

import com.skillbox.ascent.data.DetailedAthlete
import com.skillbox.ascent.data.activity.DetailedActivity
import retrofit2.Call
import retrofit2.http.*

interface StravaApi {
    @GET("api/v3/athlete")
    fun viewProfileInformation(): Call<DetailedAthlete>

    @GET("api/v3/athlete/activities")
    fun getActivities(
        // @Query("before") before: Long,
        // @Query("after") after: Long,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Call<List<DetailedActivity>>


    @FormUrlEncoded
    @POST("api/v3/activities")
    fun createActivity(
        @Field("name") name: String,
        @Field("type") type: String,
        @Field("start_date_local") startDateLocal: String,
        @Field("elapsed_time") elapsedTime: Int,
        @Field("description") description: String,
        @Field("distance") distance: Float,
        @Field("trainer") trainer: Int,
        @Field("commute") commute: Int
    ): Call<DetailedActivity>

    @FormUrlEncoded
    @PUT("api/v3/athlete")
    fun editAthleteWeight(@Field("weight") weight: Float): Call<DetailedAthlete>

}

//$ http POST "https://www.strava.com/api/v3/activities" title='value' type='value' start_date_local='value' elapsed_time='value' description='value' distance='value' trainer='value' commute='value' "Authorization: Bearer [[token]]"