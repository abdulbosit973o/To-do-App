package ru.ifr0z.notify.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.abdulbosit.apps.to_do_app.data.room.dao.BooksDao
import uz.abdulbosit.apps.to_do_app.data.room.entity.BookEntity

@Database(entities = [BookEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getBooksDao(): BooksDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun init(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "Books.db"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }

        fun getInstance() = INSTANCE!!
    }
}