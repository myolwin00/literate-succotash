package com.logbook.myolwinoo.todo.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean
)
