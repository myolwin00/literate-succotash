package com.logbook.myolwinoo.todo.features.todo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.logbook.myolwinoo.todo.data.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class TodoViewModel: ViewModel() {

    private val _todoList = MutableStateFlow<List<Todo>>(dummyTodoList)
    val todoList: StateFlow<List<Todo>> = _todoList

    var title = mutableStateOf(TextFieldValue(""))
        private set

    var description = mutableStateOf(TextFieldValue(""))
        private set

    var showBottomSheet = mutableStateOf<Boolean>(false)
        private set

    private var editingTodoId: String? = null

    var isSaveBtnEnabled = snapshotFlow {
        title.value.text.isNotBlank() && description.value.text.isNotBlank()
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

    private fun create() {
        _todoList.update { value ->
            val newTodo = Todo(
                id = UUID.randomUUID().toString(),
                title = title.value.text,
                description = description.value.text
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
                        description = description.value.text
                    )
                } else it
            }
        }
        editingTodoId = null
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

    fun delete(id: String) {
        _todoList.update {
            it.filterNot { it.id == id }
        }
    }
}

val dummyTodoList = List(6) { i ->
    Todo(
        id = (System.currentTimeMillis() + i).toString(),
        title = "Todo $i",
        description = "This is a dummy todo item created for demonstration and testing purposes. It represents a typical task within a to-do list application.".let {
            var result = it
            repeat(i) {
                result += result
            }
            result
        }
    )
}
