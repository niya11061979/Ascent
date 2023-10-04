package com.skillbox.ascent.data.db.models

object DetailActivityContract {
    const val TABLE_NAME = "activities"

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val TYPE = "type"
        const val START_DAY_LOCAL = "start_date_local"
        const val DESCRIPTION = "description"
        const val DISTANCE = "distance"
        const val ELAPCED_TIME = "elapced_time"
        const val TRAINER = "trainer"
        const val COMMUTE = "commute"
        const val ELEVATION_GAIN = "elevation_gain"
    }
}

