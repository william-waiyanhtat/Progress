package com.celestial.progress.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
import com.celestial.progress.others.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
private val counterRepository: CounterRepository
): ViewModel(),ViewModelUseCase {

    override fun createCounter(counter: Counter) {
         viewModelScope.launch {
             counterRepository.insertCounterItem(counter)
         }


    }

    override fun readCounterDetail(): Counter {
        TODO("Not yet implemented")
    }

    override fun readAllCounters(): LiveData<List<Counter>> {
       return counterRepository.observeAllCounterItem()
    }
}