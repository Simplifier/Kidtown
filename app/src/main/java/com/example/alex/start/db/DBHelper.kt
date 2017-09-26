package com.example.alex.start.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import com.example.alex.start.R
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Created by Alex on 01.09.2017.
 */
class DBHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "content.db") {
    private var dbInit: Boolean = false

    companion object {
        val pagesTable = "pages"
        private var instance: DBHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    fun initDb(ctx: Context) {
        if (dbInit) {
            return
        }

        val dbPath = "/data${Environment.getDataDirectory().absolutePath}" +
                "/com.example.alex.start/databases"
        val dbFile = dbPath + "/content.db"
        val path = File(dbPath)
        if (!path.exists()) {
            path.mkdir()
        }
        val ins: InputStream = ctx.resources.openRawResource(R.raw.content)
        val fos = FileOutputStream(dbFile)
        val buffer = ByteArray(2048)
        try {
            var count = ins.read(buffer)
            while (count > 0) {
                fos.write(buffer, 0, count)
                count = ins.read(buffer)
            }
        } finally {
            ins.close()
            fos.close()
        }
        dbInit = true
    }

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}

val Context.database: DBHelper
    get() = DBHelper.getInstance(applicationContext)