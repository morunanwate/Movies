package com.diagnal.workshop.movies.showlist

import androidx.lifecycle.ViewModel
import com.diagnal.workshop.movies.data.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShowViewModel @Inject constructor(
    private val showRepository: ShowRepository
) : ViewModel() {

}