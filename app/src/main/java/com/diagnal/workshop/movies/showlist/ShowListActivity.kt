package com.diagnal.workshop.movies.showlist

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diagnal.workshop.movies.R
import com.diagnal.workshop.movies.databinding.ActivityShowListBinding
import com.diagnal.workshop.movies.utils.AppConstant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowListActivity : AppCompatActivity() {
    private val showViewModel by viewModels<ShowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityShowListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpToolbar(binding)

        // Get the view model

        val items = showViewModel.pagingDataFlow
        val showListAdapter = ShowListAdapter()

        binding.bindState(
            showViewModel.uiState,
            showViewModel.accept,
            showListAdapter,
            resources.configuration.orientation
        )

        showViewModel.uiState.distinctUntilChanged().observe(this@ShowListActivity) {
            when (it) {
                UiState.NormalUIState -> {
                    showViewModel.accept(UiAction.ClearSearchResult)
                    hideSearchUI(binding)
                    showListAdapter.clearSearch()
                    showListAdapter.notifyDataSetChanged()
                }

                UiState.ShowingSearchUIState -> {
                    showSearchUI(binding)
                    showViewModel.accept(UiAction.TypingSearchText(binding.toolbar.searchET.text?.toString() ?: AppConstant.DEFAULT_QUERY))
                }
                is UiState.TypingQueryState -> {
                    showSearchUI(binding)
                    if (it.isSearching)
                        showListAdapter.updateSearchText(it.typedText)
                    else
                        showListAdapter.updateSearchText("")
                    showListAdapter.notifyDataSetChanged()
                }
            }

            showViewModel.showDataLivedata.distinctUntilChanged().observe(this@ShowListActivity) {
                binding.toolbar.titleTV.text = it.page.title
            }
        }

        // Collect from the Article Flow in the ViewModel, and submit it to the
        // ListAdapter.
        lifecycleScope.launch {
            // We repeat on the STARTED lifecycle because an Activity may be PAUSED
            // but still visible on the screen, for example in a multi window app
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                items.collectLatest {
                    showListAdapter.submitData(it)
                }
            }
        }
    }

    private fun setUpToolbar(binding: ActivityShowListBinding) {
        binding.toolbar.backArrowIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun hideSearchUI(binding: ActivityShowListBinding) {
        binding.toolbar.searchET.visibility = View.GONE
        binding.toolbar.searchButtonIV.visibility = View.VISIBLE
        binding.toolbar.backArrowIV.visibility = View.VISIBLE
        binding.toolbar.titleTV.visibility = View.VISIBLE

        binding.toolbar.searchButtonIV.setImageResource(R.drawable.ic_search)
        hideSoftKeyboard(this@ShowListActivity, binding.toolbar.searchET)
    }

    private fun showSearchUI(binding: ActivityShowListBinding) {
        binding.toolbar.searchET.visibility = View.VISIBLE
        binding.toolbar.searchButtonIV.visibility = View.VISIBLE
        binding.toolbar.backArrowIV.visibility = View.GONE
        binding.toolbar.titleTV.visibility = View.GONE

        binding.toolbar.searchET.requestFocus()
        binding.toolbar.searchButtonIV.setImageResource(R.drawable.ic_search_cancel)
        showSoftKeyboard(this@ShowListActivity, binding.toolbar.searchET)
    }

}

private fun ActivityShowListBinding.bindState(
    uiState: LiveData<UiState>,
    uiActions: (UiAction) -> Unit,
    showListAdapter: ShowListAdapter,
    orientation: Int
) {
    bindAdapter(showListAdapter, orientation)
    bindSearching(uiState, uiActions)
}

private fun ActivityShowListBinding.bindAdapter(showListAdapter: ShowListAdapter, orientation: Int) {
    list.adapter = showListAdapter
    val spanCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        AppConstant.LANDSCAPE_SPAN_COUNT
    else
        AppConstant.PORTRAIT_SPAN_COUNT
    list.layoutManager = GridLayoutManager(list.context, spanCount, RecyclerView.VERTICAL, false)
    val decoration = ShowSpaceDecoration(30, 90)
    list.addItemDecoration(decoration)
}


private fun ActivityShowListBinding.bindSearching(
    uiState: LiveData<UiState>,
    onSearchAction: (UiAction) -> Unit
) {
    toolbar.searchButtonIV.setOnClickListener {
        onSearchAction(UiAction.UpdateSearchUIVisibility(uiState.value == UiState.NormalUIState))
    }

    toolbar.searchET.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            //When we rotate device then UI gets recreated and we get this callback with blank value witch trigger Search UI. So to fix this
            //we apply visibility condition
            if (toolbar.searchET.isVisible)
                onSearchAction(UiAction.TypingSearchText(s?.toString() ?: ""))
        }
    })
}

fun hideSoftKeyboard(context: Context, focusedView: View?) {
    focusedView?.let { view ->
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun showSoftKeyboard(context: Context, focusedView: View?) {
    focusedView?.let { view ->
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(focusedView, 0)
    }
}