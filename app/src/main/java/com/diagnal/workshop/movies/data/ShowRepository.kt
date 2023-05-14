package com.diagnal.workshop.movies.data

import com.diagnal.workshop.movies.data.model.Show
import javax.inject.Inject

class ShowRepository@Inject constructor(private val showDataProvider: ShowDataProvider) {

    fun showPagingSource(searchText: String = "", updateShow: (Show) -> Unit) = ShowPagingSource(showDataProvider, searchText, updateShow)

}