package com.skillbox.ascent.data.db

import android.content.Context
import androidx.room.Room

object Database {
    lateinit var instance: StravaDataBase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            StravaDataBase::class.java,
            StravaDataBase.DB_NAME
        )
            .build()
    }
}