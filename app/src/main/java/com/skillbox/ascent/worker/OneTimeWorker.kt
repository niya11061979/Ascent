package com.skillbox.ascent.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.skillbox.ascent.data.Repository

class OneTimeWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val repository = Repository(context)
    override suspend fun doWork(): Result {
        return try {
            //вытаскиваем флаг из преференсов.Если в течении суток добавили будет true иначе false
            val isActivitiesAdd = repository.readStateIsActivitiesAdded()
            Log.d("msg","OneTimeWorker isActivitiesAdd  ----  $isActivitiesAdd")
            val dataSuccess = workDataOf(WORKER_SUCCESS_KEY to isActivitiesAdd)
            Result.success(dataSuccess)
        } catch (t: Throwable) {
            val dataFailure = workDataOf(WORKER_FAILURE_KEY to t.message)
            Result.failure(dataFailure)
        }
    }

    companion object {
        const val WORKER_SUCCESS_KEY = "worker_success_key"
        const val WORKER_FAILURE_KEY = "worker_failure_key"
    }
}