package com.celestial.progress.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Counter(
    val title: String,
    val startDate: String,
    val endDate: String,
    val isElapsed: Boolean,
    val color: Int,
    val note: String,

    @PrimaryKey
    val id: Int? = null
) {
}