package com.skillbox.ascent.ui.create_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.Repository
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.db.DbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    application: Application,
    private val repositoryDb: DbRepository,
    private val repository: Repository
) : AndroidViewModel(application) {
    private val isCreatedMutableLiveData = MutableLiveData<Boolean>()
    private val errorMutableLiveData = MutableLiveData<String>()
    val isCreateLiveData: LiveData<Boolean>
        get() = isCreatedMutableLiveData

    val errorLiveData: LiveData<String>
        get() = errorMutableLiveData

    fun createActivity(currentActivity: Activity) {

        viewModelScope.launch {
            try {
                repository.createActivity(
                    currentActivity,
                    {
                        isCreatedMutableLiveData.postValue(it)
                        // если добавили,то запишем true в преференсы слежения за добавлением тренрировок
                        repository.writeActivitiesAdded(true)
                    }
                ) { error -> errorMutableLiveData.postValue(error) }
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun addActivityInDb(activity: Activity) {
        viewModelScope.launch {
            try {
                repositoryDb.insertLazyActivity(activity)
            } catch (t: Throwable) {

            }
        }
    }
}