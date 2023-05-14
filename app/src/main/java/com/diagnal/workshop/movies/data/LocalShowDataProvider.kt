package com.diagnal.workshop.movies.data

import android.content.Context
import com.diagnal.workshop.movies.data.model.Show
import com.diagnal.workshop.movies.utils.AppConstant
import com.google.gson.Gson
import java.io.InputStream


class LocalShowDataProvider (private val context: Context): ShowDataProvider {

    override fun getShows(pageNum: Int): Show? {
        val stream: InputStream = when (pageNum) {
            1 -> context.assets.open(AppConstant.API_PAGE_1_FILE_NAME)
            2 -> context.assets.open(AppConstant.API_PAGE_2_FILE_NAME)
            3 -> context.assets.open(AppConstant.API_PAGE_3_FILE_NAME)
            else -> return null
        }
        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        val showJson = String(buffer)
        return Gson().fromJson(showJson, Show::class.java)
    }

}