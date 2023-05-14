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

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.recyclerview.widget.RecyclerView
import com.diagnal.workshop.movies.R
import com.diagnal.workshop.movies.data.model.Content
import com.diagnal.workshop.movies.databinding.ShowViewholderBinding

class ShowViewHolder(
    private val binding: ShowViewholderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(content: Content, searchText: String) {
        binding.apply {
            if (searchText.isNotEmpty() && content.name.contains(searchText, true)) {
                val spannableName: Spannable = SpannableString(content.name)
                val startIndex = content.name.indexOf(searchText, ignoreCase = true)
                val stopIndex = startIndex + searchText.length
                spannableName.setSpan(
                    ForegroundColorSpan(Color.YELLOW),
                    startIndex,
                    stopIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                showNameTV.text = spannableName
            } else {
                showNameTV.text = content.name
            }

            val context = showImageIV.context
            showImageIV.setImageResource(
                getImage(
                    context,
                    content.posterImage.removeSuffix(".jpg")
                )
            )
        }
    }
}


fun getImage(context: Context, imageName: String): Int {
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    return if (resourceId != 0)
        resourceId
    else
        R.drawable.placeholder_for_missing_posters
}
