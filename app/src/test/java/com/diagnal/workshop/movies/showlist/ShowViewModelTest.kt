package com.diagnal.workshop.movies.showlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diagnal.workshop.movies.data.ShowRepository
import com.diagnal.workshop.movies.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ShowViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var showRepository: ShowRepository


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun test_GetUIState_expectedNormalState() = runTest {
        val showViewModel = ShowViewModel(showRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val value = showViewModel.uiState.getOrAwaitValue()
        Assert.assertEquals(true, value is UiState.NormalUIState)
    }

    @Test
    fun test_GetUIState_expectedShowingSearchUIState() = runTest {
        val showViewModel = ShowViewModel(showRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        showViewModel.accept(UiAction.UpdateSearchUIVisibility(true))
        val value = showViewModel.uiState.getOrAwaitValue()
        Assert.assertEquals(true, value is UiState.ShowingSearchUIState)
    }

    @Test
    fun test_GetUIState_expectedTypingQueryStateSearching() = runTest {
        val showViewModel = ShowViewModel(showRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        showViewModel.accept(UiAction.TypingSearchText("Text Greater than 3 char"))
        val value = showViewModel.uiState.getOrAwaitValue()
        Assert.assertEquals(true, value is UiState.TypingQueryState)
        val typingUIState = value as UiState.TypingQueryState
        Assert.assertEquals(true, typingUIState.isSearching)
        Assert.assertEquals("Text Greater than 3 char", typingUIState.typedText)
    }

    @Test
    fun test_GetUIState_expectedTypingQueryStateNotSearching() = runTest {
        val showViewModel = ShowViewModel(showRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        showViewModel.accept(UiAction.TypingSearchText("Ab"))
        val value = showViewModel.uiState.getOrAwaitValue()
        Assert.assertEquals(true, value is UiState.TypingQueryState)
        val typingUIState = value as UiState.TypingQueryState
        Assert.assertEquals(false, typingUIState.isSearching)
        Assert.assertEquals("Ab", typingUIState.typedText)
    }

    @Test
    fun test_GetUIState_expectedCloseSearchUIGetNormalUIState() = runTest {
        val showViewModel = ShowViewModel(showRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        showViewModel.accept(UiAction.UpdateSearchUIVisibility(false))
        val value = showViewModel.uiState.getOrAwaitValue()
        Assert.assertEquals(true, value is UiState.NormalUIState)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


}