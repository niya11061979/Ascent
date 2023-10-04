package com.skillbox.ascent.data.db

import com.skillbox.ascent.data.DetailedAthlete
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.activity.DetailedActivity
import javax.inject.Inject

class DbRepository @Inject constructor() {
    private val activityDbDao = Database.instance.detailActivityDao()
    private val lazyActivityDao = Database.instance.lazyActivityDao()
    private val athleteDao = Database.instance.detailAthleteDao()

    suspend fun insertLazyActivity(activity: Activity) =
        lazyActivityDao.insertLazyActivity(activity)

    suspend fun insertAthlete(profileInformation: DetailedAthlete) {
        athleteDao.insertAthlete(profileInformation)
    }


    suspend fun clearActivitiesDb() = activityDbDao.deleteAllActivities()
    suspend fun loadAthleteFromDb(): DetailedAthlete = athleteDao.getAthlete()
    suspend fun getLazyActivities(): List<Activity> = lazyActivityDao.getLazyActivities()
    suspend fun loadActivitiesFromDb(): List<DetailedActivity> = activityDbDao.getAllActivities()
    suspend fun saveActivities(list: List<DetailedActivity>) = activityDbDao.insertActivities(list)
    suspend fun removeLazyActivity(DateTime: String) = lazyActivityDao.removeLazyActivity(DateTime)


}