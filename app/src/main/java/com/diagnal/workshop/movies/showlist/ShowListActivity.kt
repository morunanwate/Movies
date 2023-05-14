package com.diagnal.workshop.movies.showlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.diagnal.workshop.movies.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)
    }
}