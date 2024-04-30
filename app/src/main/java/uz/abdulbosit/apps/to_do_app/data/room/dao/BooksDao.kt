package uz.abdulbosit.apps.to_do_app.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uz.abdulbosit.apps.to_do_app.data.room.entity.BookEntity


@Dao
interface BooksDao {
    @Query("SELECT * FROM ToDoData Where isFinished = 0")
    fun getAllBooksFromLocal(): List<BookEntity>
    @Query("SELECT * FROM ToDoData Where isFinished = 1")
    fun getAllBooksFromLocalFinished(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(data: BookEntity)
    @Update
    fun updateBooks(data: BookEntity)
    @Query("DELETE FROM ToDoData WHERE id = :id")
    fun deleteToDo(id:Long)


    @Query("DELETE FROM ToDoData")
    fun nukeTable()

}