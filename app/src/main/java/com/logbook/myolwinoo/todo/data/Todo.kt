package com.logbook.myolwinoo.todo.data;

import android.text.format.DateUtils

data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean
) {
    val timeAgo: String
        get() = DateUtils.getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS,

        ).toString()
}