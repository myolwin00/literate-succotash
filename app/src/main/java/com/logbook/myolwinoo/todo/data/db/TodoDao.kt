package com.logbook.myolwinoo.todo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos")
    fun getAll(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos where id=:id")
    suspend fun get(id: String): TodoEntity?

    @Insert
    suspend fun insertAll(vararg todos: TodoEntity)

    @Query("update todos set is_completed = not is_completed where id=:id")
    suspend fun toggleComplete(id: String)

    @Query("update todos set title=:title, description=:description, timestamp=:timestamp where id=:id")
    suspend fun update(
        id: String,
        title: String,
        description: String,
        timestamp: Long
    )

    @Query("delete from todos where id=:id")
    suspend fun delete(id: String)
}