package com.skillbox.ascent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.skillbox.ascent.R


object NotificationChannels {
    const val MESSAGE_CHANNEL_ID = "messages"
    fun create(context: Context) {
        if (haveQ())  createMessageChannel(context)    //проверка версии Android
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMessageChannel(context: Context) {
        val name = context.getString(R.string.reminder)
        val channelDescription = context.getString(R.string.dont_creat_activity)
        val priority = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(MESSAGE_CHANNEL_ID, name, priority).apply {
            description = channelDescription
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
        }
        NotificationManagerCompat.from(context).cancelAll()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}