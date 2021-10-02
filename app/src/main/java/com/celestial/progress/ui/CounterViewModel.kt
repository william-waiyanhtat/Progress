package com.celestial.progress.ui


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.RepoStatus
import com.celestial.progress.others.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
        private val counterRepository: CounterRepository
) : ViewModel(), ViewModelUseCase {

    val TAG = CounterViewModel::class.java.name

    var editCounter: Counter? = null


    override fun createCounter(counter: Counter): LiveData<Resource<Long>> {

        val result = MutableLiveData<Resource<Long>>()

        viewModelScope.launch {
            counterRepository.insertCounterItem(counter, object : RepoStatus {

                override fun success(obj: Any, msg: String?) {
                    Log.d(TAG, "Success Insert: ${obj as Long}")
                    result.postValue(Resource.success(obj, msg))
                }

                override fun loading(msg: String) {

                }

                override fun failed(obj: Any, errMsg: String) {
                    result.postValue(Resource.error(errMsg, null))
                }
            })
        }
        return result
    }

    override fun updateCounter(counter: Counter): LiveData<Resource<Int>> {
        val result = MutableLiveData<Resource<Int>>()

        viewModelScope.launch {
            counterRepository.updateCounter(counter,object:  RepoStatus{
                override fun success(obj: Any, msg: String?) {
                    result.postValue(Resource.success(obj as Int, msg))
                }

                override fun loading(msg: String) {

                }

                override fun failed(obj: Any, errMsg: String) {
                    result.postValue(Resource.error(errMsg, null))
                }

            })

        }
        return result
    }

    override fun insertAll(counters: List<Counter>) {
        viewModelScope.launch {
            counterRepository.insertAllCounters(counters)
        }

    }


    override fun readCounterDetail(): Counter {
        TODO("Not yet implemented")
    }

    override fun readAllCounters(): LiveData<List<Counter>> {
        Log.d(TAG, "read live data")
        return counterRepository.observeAllCounterItem()
    }



    override fun readArchiveCounters(): LiveData<List<Counter>> {
        return counterRepository.observeAllArchiveCounterItem()
    }

    override fun deleteCounter(counter: Counter) {
        viewModelScope.launch {
            counterRepository.deleteCounterItem(counter)
        }

    }

    override fun fetchNotificationOnCounterList(): LiveData<List<Counter>> {
           return counterRepository.fetchCounterListWhichRequiredNotification()
    }

    override fun updateCounterForNotificationById(id: Int, isNotify: Boolean) {
        viewModelScope.launch {
            counterRepository.updateCounterNotificationStatus(id,isNotify)

        }
    }
}