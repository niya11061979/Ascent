package com.skillbox.ascent.data.db.models

object ActivityContract {
    const val TABLE_NAME = "activity_lazy_loading"

    object Columns {
        const val NAME = "name"
        const val TYPE = "type"
        const val ELAPCED_TIME = "elapced_time"
        const val START_DAY_LOCAL = "start_date_local"
        const val DESCRIPTION = "description"
        const val DISTANCE = "distance"
        const val TRAINER = "trainer"
        const val COMMUTE = "commute"
    }
}