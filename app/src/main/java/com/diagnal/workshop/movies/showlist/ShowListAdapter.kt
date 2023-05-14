/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.diagnal.workshop.movies.showlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.diagnal.workshop.movies.data.model.Content
import com.diagnal.workshop.movies.databinding.ShowViewholderBinding


class ShowListAdapter : PagingDataAdapter<Content, ShowViewHolder>(ARTICLE_DIFF_CALLBACK) {
    private var searchText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder =
        ShowViewHolder(
            ShowViewholderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val tile = getItem(position)
        if (tile != null) {
            holder.bind(tile, searchText)
        }
    }

    companion object {
        private val ARTICLE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Content>() {
            override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem.name == newItem.name && oldItem.posterImage == newItem.posterImage

            override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem == newItem
        }
    }

    fun updateSearchText(searchText: String) {
        this.searchText = searchText
    }

    fun clearSearch() {
        this.searchText = ""
    }
}
