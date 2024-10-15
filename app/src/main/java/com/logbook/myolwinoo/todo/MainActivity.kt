@file:OptIn(ExperimentalMaterial3Api::class)

package com.logbook.myolwinoo.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.logbook.myolwinoo.todo.data.Todo
import com.logbook.myolwinoo.todo.features.todo.TodoViewModel
import com.logbook.myolwinoo.todo.features.todo.dummyTodoList
import com.logbook.myolwinoo.todo.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                val viewModel: TodoViewModel = viewModel()
                val todos = viewModel.todoList.collectAsStateWithLifecycle()
                val isSaveBtnEnabled = viewModel.isSaveBtnEnabled.collectAsStateWithLifecycle(false)

                TodoScreen(
                    todos = todos.value,
                    isSaveBtnEnabled = isSaveBtnEnabled.value,

                    title = viewModel.title.value,
                    onTitleChange = viewModel::onTitleChange,

                    description = viewModel.description.value,
                    onDescriptionChange = viewModel::onDescriptionChange,

                    onSave = viewModel::save,

                    onEdit = viewModel::startEdit,
                    onDelete = viewModel::delete,

                    showBottomSheet = viewModel.showBottomSheet.value,
                    onShowSheet = viewModel::showTodoSheet,
                    onDismissSheet = viewModel::dismissTodoSheet
                )
            }
        }
    }
}

@Composable
private fun TodoScreen(
    todos: List<Todo>,
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,

    onSave: () -> Unit,
    isSaveBtnEnabled: Boolean,

    showBottomSheet: Boolean,
    onShowSheet: () -> Unit,
    onDismissSheet: () -> Unit,

    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit
) {
    val listState = rememberLazyListState()
    val expandedFab = remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text("All Todos")
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onShowSheet() },
                expanded = expandedFab.value,
                icon = { Icon(Icons.Filled.Add, "Create Button") },
                text = { Text(text = "Create") },
            )
        }
    ) { innerPadding ->

        if (showBottomSheet) {
            TodoSheet(
                onDismiss = onDismissSheet,

                title = title,
                onTitleChange = onTitleChange,

                description = description,
                onDescriptionChange = onDescriptionChange,
                onSave = onSave,
                isSaveBtnEnabled = isSaveBtnEnabled
            )
        }

        LazyColumn(
            contentPadding = innerPadding,
            state = listState
        ) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    text = "Today",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            todoList(
                todos = todos,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

private fun LazyListScope.todoList(
    todos: List<Todo>,
    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit
) {
    items(
        items = todos,
        key = { it.id }
    ) {
        ToDoItem(
            todo = it,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}

@Composable
fun ToDoItem(
    modifier: Modifier = Modifier,
    todo: Todo,
    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = todo.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { onEdit(todo.id) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Button",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { onDelete(todo.id) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Button",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TodoSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,

    isSaveBtnEnabled: Boolean,
    onSave: () -> Unit,

    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,

    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Box {
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    value = title,
                    onValueChange = onTitleChange,
                    textStyle = MaterialTheme.typography.bodyLarge
                        .copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                )
                if (title.text.isEmpty()) {
                    Text(
                        text = "New todo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Box {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    value = description,
                    onValueChange = onDescriptionChange,
                    minLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                )
                if (description.text.isEmpty()) {
                    Text(
                        text = "Add details...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Button(
                modifier = Modifier
                    .align(Alignment.End),
                enabled = isSaveBtnEnabled,
                onClick = {
                    onSave()
                    onDismiss()
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Preview
@Composable
private fun TodoScreenPreview() {
    TodoScreen(
        todos = dummyTodoList,
        title = TextFieldValue(""),
        onTitleChange = {},
        description = TextFieldValue(""),
        onDescriptionChange = {},
        isSaveBtnEnabled = false,
        onSave = {},

        onEdit = {},
        onDelete = {},

        showBottomSheet = false,
        onShowSheet = {},
        onDismissSheet = {}
    )
}