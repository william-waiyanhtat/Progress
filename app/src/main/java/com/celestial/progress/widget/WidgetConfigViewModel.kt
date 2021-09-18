package com.celestial.progress.widget

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.celestial.progress.data.CounterRepository
import com.celestial.progress.data.model.Counter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(private val counterRepository: CounterRepository): ViewModel() {

    fun observeCounterLiveData(): LiveData<List<Counter>>{
        return counterRepository.observeAllCounterItem()
    }

}