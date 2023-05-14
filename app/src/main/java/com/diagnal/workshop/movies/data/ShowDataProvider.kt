package com.diagnal.workshop.movies.data

import com.diagnal.workshop.movies.data.model.Show

interface ShowDataProvider {
    fun getShows(pageNum: Int): Show?
}