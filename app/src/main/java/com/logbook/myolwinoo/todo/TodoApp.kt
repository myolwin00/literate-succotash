package com.logbook.myolwinoo.todo

import android.app.Application
import com.logbook.myolwinoo.todo.data.db.TodoDatabase

class TodoApp: Application() {

    val todoDb by lazy {
        TodoDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}