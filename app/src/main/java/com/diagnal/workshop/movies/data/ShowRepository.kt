package com.diagnal.workshop.movies.data

import javax.inject.Inject

class ShowRepository@Inject constructor(private val showDataProvider: ShowDataProvider) {
}