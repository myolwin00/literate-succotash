package com.logbook.myolwinoo.todo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        private const val DATABASE_NAME = "todo_db"

        fun getInstance(applicationContext: Context): TodoDatabase {
            return Room.databaseBuilder(
                applicationContext,
                TodoDatabase::class.java, DATABASE_NAME
            ).build()
        }
    }

}