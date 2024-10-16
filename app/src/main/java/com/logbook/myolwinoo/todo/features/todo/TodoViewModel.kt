package com.logbook.myolwinoo.todo.features.todo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.logbook.myolwinoo.todo.data.Todo
import com.logbook.myolwinoo.todo.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repo: TodoRepository
): ViewModel() {

    val todoList: MutableStateFlow<List<Todo>> = MutableStateFlow(emptyList())
    val completedTodoList: MutableStateFlow<List<Todo>> = MutableStateFlow(emptyList())

    var title = mutableStateOf(TextFieldValue(""))
        private set

    var description = mutableStateOf(TextFieldValue(""))
        private set

    var showBottomSheet = mutableStateOf(false)
        private set

    private var editingTodoId: String? = null

    val listMode = mutableStateOf(ListMode.List)

    var isSaveBtnEnabled = snapshotFlow {
        title.value.text.isNotBlank() && description.value.text.isNotBlank()
    }

    init {
        repo.getAll()
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

    fun save() {
        editingTodoId?.let {
            update(it)
        } ?: create()

        title.value = TextFieldValue("")
        description.value = TextFieldValue("")
    }

    fun startEdit(id: String) {
        viewModelScope.launch {
            repo.get(id)
                ?.also {
                    editingTodoId = id
                    title.value = TextFieldValue(it.title, TextRange(it.title.length))
                    description.value = TextFieldValue(it.description, TextRange(it.description.length))
                    showTodoSheet()
                }
        }
    }

    fun toggleComplete(id: String) {
        viewModelScope.launch {
            repo.toggleComplete(id)
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.delete(id)
        }
    }

    private fun create() {
        viewModelScope.launch {
            repo.create(
                title = title.value.text,
                description = description.value.text
            )
        }
    }

    private fun update(id: String) {
        viewModelScope.launch {
            repo.update(
                id = id,
                title = title.value.text,
                description = description.value.text
            )
        }
    }

    class Factory(
        private val repo: TodoRepository
    ):  ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return TodoViewModel(
                repo,
            ) as T
        }
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
