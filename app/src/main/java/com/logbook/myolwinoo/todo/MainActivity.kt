package com.logbook.myolwinoo.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logbook.myolwinoo.todo.data.TodoRepository
import com.logbook.myolwinoo.todo.features.todo.TodoScreen
import com.logbook.myolwinoo.todo.features.todo.TodoViewModel
import com.logbook.myolwinoo.todo.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val db = (application as TodoApp).todoDb
        val repo = TodoRepository(db.todoDao())

        setContent {
            TodoTheme {
                val viewModel: TodoViewModel = viewModel(factory = TodoViewModel.Factory(repo))

                val todayTodoList = viewModel.todayTodoList.collectAsStateWithLifecycle(emptyList())
                val upcomingTodoList =
                    viewModel.upcomingTodoList.collectAsStateWithLifecycle(emptyList())
                val completedTodoList =
                    viewModel.completedTodoList.collectAsStateWithLifecycle(emptyList())

                val isSaveBtnEnabled = viewModel.isSaveBtnEnabled.collectAsStateWithLifecycle(false)

                TodoScreen(
                    todayTodoList = todayTodoList.value,
                    upcomingTodoList = upcomingTodoList.value,
                    completedTodoList = completedTodoList.value,

                    isSaveBtnEnabled = isSaveBtnEnabled.value,

                    title = viewModel.title.value,
                    onTitleChange = viewModel::onTitleChange,

                    description = viewModel.description.value,
                    onDescriptionChange = viewModel::onDescriptionChange,

                    onSave = viewModel::save,

                    onEdit = viewModel::startEdit,
                    onDelete = viewModel::delete,
                    onToggleComplete = viewModel::toggleComplete,

                    showBottomSheet = viewModel.showBottomSheet.value,
                    onShowSheet = viewModel::showTodoSheet,
                    onDismissSheet = viewModel::dismissTodoSheet,

                    listMode = viewModel.listMode.value,
                    onToggleListMode = viewModel::toggleListMode,
                )
            }
        }
    }
}