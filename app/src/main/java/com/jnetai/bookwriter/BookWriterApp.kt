package com.jnetai.bookwriter

import android.app.Application
import com.jnetai.bookwriter.data.AppDatabase

class BookWriterApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}