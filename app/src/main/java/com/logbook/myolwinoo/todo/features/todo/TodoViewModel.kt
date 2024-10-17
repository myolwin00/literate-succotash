package com.logbook.myolwinoo.todo.features.todo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logbook.myolwinoo.todo.data.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.UUID

class TodoViewModel : ViewModel() {

    private val _todoList = MutableStateFlow(dummyTodoList)

    val todoList: MutableStateFlow<List<Todo>> = MutableStateFlow(emptyList())
    val completedTodoList: MutableStateFlow<List<Todo>> = MutableStateFlow(emptyList())

    var title = mutableStateOf(TextFieldValue(""))
        private set

    var description = mutableStateOf(TextFieldValue(""))
        private set

    var showBottomSheet = mutableStateOf(false)
        private set
    var confirmDeleteId = mutableStateOf<String?>(null)
        private set

    private var editingTodoId: String? = null

    val listMode = mutableStateOf(ListMode.List)

    var isSaveBtnEnabled = snapshotFlow {
        title.value.text.isNotBlank() && description.value.text.isNotBlank()
    }

    init {
        _todoList
            .onEach {
                val (completed, todo) = it.partition { todo -> todo.isCompleted }
                todoList.update { todo }
                completedTodoList.update { completed }
            }
            .launchIn(viewModelScope)
    }

    fun toggleListMode() {
        listMode.value = when (listMode.value) {
            ListMode.List -> ListMode.Grid
            ListMode.Grid -> ListMode.List
        }
    }

    fun onTitleChange(value: TextFieldValue) {
        title.value = value
    }

    fun onDescriptionChange(value: TextFieldValue) {
        description.value = value
    }

    fun showTodoSheet() {
        showBottomSheet.value = true
    }

    fun dismissTodoSheet() {
        showBottomSheet.value = false
    }

    fun showConfirmDelete(id: String) {
        confirmDeleteId.value = id
    }

    fun hideConfirmDelete() {
        confirmDeleteId.value = null
    }

    fun save() {
        editingTodoId?.let {
            update(it)
        } ?: create()

        title.value = TextFieldValue("")
        description.value = TextFieldValue("")
    }

    fun startEdit(id: String) {
        _todoList.value.find { it.id == id }
            ?.also {
                editingTodoId = id
                title.value = TextFieldValue(it.title, TextRange(it.title.length))
                description.value = TextFieldValue(it.description, TextRange(it.description.length))
                showTodoSheet()
            }
    }

    fun toggleComplete(id: String) {
        _todoList.update {
            it.map {
                if (it.id == id) {
                    it.copy(isCompleted = !it.isCompleted)
                } else it
            }
        }
    }

    fun delete(id: String) {
        _todoList.update {
            it.filterNot { it.id == id }
        }
    }

    private fun create() {
        _todoList.update { value ->
            val newTodo = Todo(
                id = UUID.randomUUID().toString(),
                title = title.value.text,
                description = description.value.text,
                timestamp = System.currentTimeMillis(),
                isCompleted = false
            )
            value + listOf(newTodo)
        }
    }

    private fun update(id: String) {
        _todoList.update { value ->
            value.map {
                if (it.id == id) {
                    it.copy(
                        title = title.value.text,
                        description = description.value.text,
                        timestamp = System.currentTimeMillis()
                    )
                } else it
            }
        }
        editingTodoId = null
    }
}

val dummyTodoList = List(6) { i ->
    Todo(
        id = (System.currentTimeMillis() + i).toString(),
        title = "Todo $i",
        description = "This is a dummy todo item created for demonstration and testing purposes. It represents a typical task within a to-do list application.",
        timestamp = System.currentTimeMillis(),
        isCompleted = i % 2 == 0
    )
}
