package com.skillbox.ascent.ui.share

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.contact.Contact
import com.skillbox.ascent.data.contact.ContactRepository
import com.skillbox.ascent.data.db.DbRepository
import com.skillbox.ascent.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    application: Application,
    private val contactRepository: ContactRepository,
    private val dbRepository: DbRepository
) : AndroidViewModel(application) {
    private val contactsMutableLiveData = MutableLiveData<List<Contact>>()
    private val isLoadingMutableLiveData = SingleLiveEvent<Boolean>()
    private val idAthleteMutableLiveData = MutableLiveData<Int>()
    val contactsLiveData: LiveData<List<Contact>>
        get() = contactsMutableLiveData
    val isLoadingLiveData: LiveData<Boolean>
        get() = isLoadingMutableLiveData
    val idAthleteLiveData: LiveData<Int>
        get() = idAthleteMutableLiveData

    fun loadContacts() {
        viewModelScope.launch {
            try {
                isLoadingMutableLiveData.postValue(true)
                contactsMutableLiveData.postValue(contactRepository.getAllContacts())
            } catch (t: Throwable) {
                contactsMutableLiveData.postValue(emptyList())
            } finally {
                isLoadingMutableLiveData.postValue(false)
            }
        }
    }

    fun getIdAthleteId() {
        viewModelScope.launch {
            try {
                val athlete = dbRepository.loadAthleteFromDb()
                idAthleteMutableLiveData.postValue(athlete.id)
            } catch (t: Throwable) {

            }
        }
    }
}