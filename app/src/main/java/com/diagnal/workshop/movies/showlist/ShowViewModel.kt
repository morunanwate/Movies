package com.diagnal.workshop.movies.showlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diagnal.workshop.movies.data.ShowRepository
import com.diagnal.workshop.movies.data.model.Content
import com.diagnal.workshop.movies.data.model.Show
import com.diagnal.workshop.movies.utils.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20

@HiltViewModel
class ShowViewModel @Inject constructor(
    private val showRepository: ShowRepository
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Content>>

    val uiState: LiveData<UiState>

    val accept: (UiAction) -> Unit

    val showDataLivedata: LiveData<Show>

    private val updateShow: (Show) -> Unit

    init {
        val uiStateMutable = MutableLiveData<UiState>(UiState.NormalUIState)
        val showDataMutableLD = MutableLiveData<Show>()

        uiState = uiStateMutable
        showDataLivedata = showDataMutableLD

        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.TypingSearchText>()
            .distinctUntilChanged()
            .onStart {
                emit(UiAction.TypingSearchText(AppConstant.DEFAULT_QUERY))
            }
        pagingDataFlow = searches
            .distinctUntilChanged()
            .flatMapLatest {
                val searchText =
                    if (it.typeText.length >= AppConstant.NUM_CHAR_REQUIRED_TO_SEARCH)
                        it.typeText
                    else
                        AppConstant.DEFAULT_QUERY
                searchShows(searchText = searchText)
            }
            .cachedIn(viewModelScope)

        accept = { action ->
            when (action) {
                is UiAction.UpdateSearchUIVisibility -> {
                    val state = if (action.showSearchUI)
                        UiState.ShowingSearchUIState
                    else
                        UiState.NormalUIState
                    uiStateMutable.postValue(state)
                }

                UiAction.ClearSearchResult -> searchDefaultQuery(actionStateFlow)

                is UiAction.TypingSearchText -> {
                    val typeText = action.typeText
                    if (typeText.length >= AppConstant.NUM_CHAR_REQUIRED_TO_SEARCH) {
                        uiStateMutable.postValue(UiState.TypingQueryState(typeText, true))
                        viewModelScope.launch {
                            actionStateFlow.emit(action)
                        }
                    } else {
                        uiStateMutable.postValue(UiState.TypingQueryState(typeText, false))
                        searchDefaultQuery(actionStateFlow)
                    }
                }
            }
        }

        updateShow = {
            showDataMutableLD.postValue(it)
        }
    }

    private fun searchDefaultQuery(actionFlow: MutableSharedFlow<UiAction>) {
        viewModelScope.launch {
            actionFlow.emit(UiAction.TypingSearchText(AppConstant.DEFAULT_QUERY))
        }
    }


    private fun searchShows(searchText: String = ""): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
            pagingSourceFactory = { showRepository.showPagingSource(searchText, updateShow) }
        ).flow.cachedIn(viewModelScope)
    }

}

sealed class UiAction {
    data class TypingSearchText(val typeText: String) : UiAction()

    data class UpdateSearchUIVisibility(val showSearchUI: Boolean) : UiAction()

    object ClearSearchResult : UiAction()
}


sealed class UiState {

    object NormalUIState : UiState()

    object ShowingSearchUIState : UiState()

    data class TypingQueryState(
        val typedText: String,
        val isSearching: Boolean
    ) : UiState()

}