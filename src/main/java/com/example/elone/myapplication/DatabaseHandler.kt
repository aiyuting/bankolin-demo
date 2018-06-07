package com.example.elone.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "yee.db", null, 1) {
    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun Instance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(database: SQLiteDatabase) {
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}

val Context.database: DatabaseHelper
    get() = DatabaseHelper.Instance(applicationContext)