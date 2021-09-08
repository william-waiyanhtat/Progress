package com.celestial.progress.ui


import androidx.lifecycle.ViewModel
import com.celestial.progress.data.CounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
private val counterRepository: CounterRepository
): ViewModel() {




}