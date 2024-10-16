package com.logbook.myolwinoo.todo.data

import com.logbook.myolwinoo.todo.data.db.TodoDao
import com.logbook.myolwinoo.todo.data.db.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TodoRepository(
    private val todoDao: TodoDao
) {
    fun getAll(): Flow<List<Todo>> {
        return todoDao.getAll()
            .map { list -> list.map { mapDomain(it) } }
    }

    suspend fun get(id: String): Todo? {
        return todoDao.get(id)
            ?.let { mapDomain(it) }
    }

    suspend fun create(
        title: String,
        description: String
    ) {
        val newTodo = TodoEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            timestamp = System.currentTimeMillis(),
            isCompleted = false
        )
        todoDao.insertAll(newTodo)
    }

    suspend fun toggleComplete(id: String) {
        todoDao.toggleComplete(id)
    }

    suspend fun update(
        id: String,
        title: String,
        description: String
    ) {
        todoDao.update(
            id = id,
            title = title,
            description = description,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun delete(id: String) {
        todoDao.delete(id)
    }

    private fun mapDomain(entity: TodoEntity): Todo {
        return Todo(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            isCompleted = entity.isCompleted,
            timestamp = entity.timestamp
        )
    }
}