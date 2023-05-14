package com.diagnal.workshop.movies.data

import com.diagnal.workshop.movies.data.model.Show


//Here we pass show while creating so that we can easily pass blank show
class FakeShowDataProvider(private val show: Show?): ShowDataProvider {
    override fun getShows(pageNum: Int): Show? {
        return show
    }
}