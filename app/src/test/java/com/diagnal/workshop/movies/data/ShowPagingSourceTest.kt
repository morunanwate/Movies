package com.diagnal.workshop.movies.data

import androidx.paging.PagingSource
import com.diagnal.workshop.movies.data.model.Content
import com.diagnal.workshop.movies.data.model.ContentItems
import com.diagnal.workshop.movies.data.model.Page
import com.diagnal.workshop.movies.data.model.Show
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ShowPagingSourceTest {

    private val pageLoadSize = 2

    private val listOfContent = listOf(
        Content("name1", "img1"),
        Content("name2", "img2"),
    )
    private val mockShow = Show(
        Page(
            ContentItems(listOfContent),
            1.toString(),
            pageLoadSize.toString(),
            "Show Test",
            50.toString()
        )
    )


    private val fakeShowDataProvider = FakeShowDataProvider(mockShow)

    @Test
    fun loadReturnsPageWhenOnSuccessfulLoad() = runTest {
        val pagingSource = ShowPagingSource(fakeShowDataProvider, "") {
            //Ignore Show Callback Lambda
        }

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = listOf(listOfContent[0], listOfContent[1]),
                prevKey = null,
                nextKey = 2
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = pageLoadSize,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun loadSearchWithCharMoreThan3() = runTest {
        val pagingSource = ShowPagingSource(fakeShowDataProvider, "name1") {
            //Ignore Show Callback Lambda
        }

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = listOf(listOfContent[0]),
                prevKey = null,
                nextKey = 2
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = pageLoadSize,
                    placeholdersEnabled = false
                )
            )
        )
    }


    //Here No Search should be perform as search query is less than 3 character
    @Test
    fun loadSearchWithCharLessThan3() = runTest {
        val pagingSource = ShowPagingSource(fakeShowDataProvider, "na") {
            //Ignore Show Callback Lambda
        }

        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = listOfContent,
                prevKey = null,
                nextKey = 2
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = pageLoadSize,
                    placeholdersEnabled = false
                )
            )
        )
    }

}