package com.celestial.progress.data


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
private val counterRepository: CounterRepository
): ViewModel() {




}