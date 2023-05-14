package com.diagnal.workshop.movies.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diagnal.workshop.movies.data.model.Content
import com.diagnal.workshop.movies.data.model.Show
import com.google.gson.JsonSyntaxException
import java.io.IOException

// The initial key used for loading.
// This is the article id of the first article that will be loaded
private const val STARTING_PAGE_NUM = 1

class ShowPagingSource(private val showDataProvider: ShowDataProvider, private val searchText: String = "", private val updateShow: (Show) -> Unit) : PagingSource<Int, Content>() {

    override fun getRefreshKey(state: PagingState<Int, Content>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Content> {
        // Start paging with the STARTING_KEY if this is the first load
        val pageNum = params.key ?: STARTING_PAGE_NUM

        return try {
            val show = showDataProvider.getShows(pageNum)
            val contentList : MutableList<Content> = mutableListOf()
            val nextKey = if ((show == null) || show.page.contentItems.content.isEmpty()) {
                null
            } else {
                pageNum + 1
            }
            show?.let {
                updateShow(it)
                if (searchText.length >= 3) {
                    for (content : Content in it.page.contentItems.content) {
                        if (content.name.contains(searchText, true))
                            contentList.add(content)
                    }
                } else {
                    contentList.addAll(it.page.contentItems.content)
                }
            }

            LoadResult.Page(
                data = contentList,
                prevKey = if (pageNum == STARTING_PAGE_NUM) null else pageNum - 1,
                nextKey = nextKey
            )
        }  catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: JsonSyntaxException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}