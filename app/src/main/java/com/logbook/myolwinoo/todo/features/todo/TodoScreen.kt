@file:OptIn(ExperimentalMaterial3Api::class)

package com.logbook.myolwinoo.todo.features.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.logbook.myolwinoo.todo.R
import com.logbook.myolwinoo.todo.data.Todo

@Composable
fun TodoScreen(
    todayTodoList: List<Todo>,
    upcomingTodoList: List<Todo>,
    completedTodoList: List<Todo>,
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,

    listMode: ListMode,
    onToggleListMode: () -> Unit,

    onSave: () -> Unit,
    isSaveBtnEnabled: Boolean,

    showBottomSheet: Boolean,
    onShowSheet: () -> Unit,
    onDismissSheet: () -> Unit,

    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit,
    onToggleComplete: (id: String) -> Unit,
) {
    val listState = rememberLazyStaggeredGridState()
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
                    Text("All Tasks")
                },
                actions = {
                    IconButton(onClick = onToggleListMode) {
                        Icon(
                            painter = painterResource(
                                when (listMode) {
                                    ListMode.Grid -> R.drawable.ic_list
                                    ListMode.List -> R.drawable.ic_grid
                                }
                            ),
                            contentDescription = "List mode button"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onShowSheet() },
                expanded = expandedFab.value,
                icon = { Icon(painterResource(R.drawable.ic_add_task), "Create Button") },
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

        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 100.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            columns = StaggeredGridCells.Fixed(
                when (listMode) {
                    ListMode.Grid -> 2
                    ListMode.List -> 1
                }
            ),
            state = listState,
        ) {
            todoSection(
                title = "Today",
                todos = todayTodoList,
                onEdit = onEdit,
                onDelete = onDelete,
                onToggleComplete = onToggleComplete
            )

            todoSection(
                title = "Upcoming",
                todos = upcomingTodoList,
                onEdit = onEdit,
                onDelete = onDelete,
                onToggleComplete = onToggleComplete
            )

            todoSection(
                title = "Completed",
                todos = completedTodoList,
                onEdit = onEdit,
                onDelete = onDelete,
                onToggleComplete = onToggleComplete
            )
        }
    }
}

private fun LazyStaggeredGridScope.todoSection(
    title: String,
    todos: List<Todo>,
    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit,
    onToggleComplete: (id: String) -> Unit,
) {
    if (todos.isNotEmpty()) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        todoList(
            todos = todos,
            onEdit = onEdit,
            onDelete = onDelete,
            onToggleComplete = onToggleComplete
        )
    }
}

private fun LazyStaggeredGridScope.todoList(
    todos: List<Todo>,
    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit,
    onToggleComplete: (id: String) -> Unit,
) {
    items(
        items = todos,
        key = { it.id }
    ) {
        TodoItem(
            todo = it,
            onEdit = onEdit,
            onDelete = onDelete,
            onToggleComplete = onToggleComplete
        )
    }
}

@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: Todo,
    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit,
    onToggleComplete: (id: String) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                )
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { onToggleComplete(todo.id) }
                ) {
                    Icon(
                        painter = painterResource(if (todo.isCompleted) R.drawable.ic_completed else R.drawable.ic_circle),
                        contentDescription = "Delete Button",
                        modifier = Modifier.size(16.dp),
                        tint = if (todo.isCompleted) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = todo.description,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = todo.timeAgo,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { onEdit(todo.id) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit Button",
                        modifier = Modifier.size(16.dp),
                    )
                }
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { onDelete(todo.id) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete Button",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TodoItemPreview() {
    TodoItem(
        todo = dummyTodoList.first().copy(isCompleted = false),
        onEdit = {},
        onDelete = {},
        onToggleComplete = {}
    )
}

@Preview
@Composable
private fun TodoItemPreview_Completed() {
    TodoItem(
        todo = dummyTodoList.first().copy(isCompleted = true),
        onEdit = {},
        onDelete = {},
        onToggleComplete = {}
    )
}

@Preview
@Composable
private fun TodoScreenPreview() {
    TodoScreen(
        todayTodoList = dummyTodoList,
        upcomingTodoList = dummyTodoList,
        completedTodoList = dummyTodoList,
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
        onDismissSheet = {},

        listMode = ListMode.List,
        onToggleListMode = {},
        onToggleComplete = {}
    )
}