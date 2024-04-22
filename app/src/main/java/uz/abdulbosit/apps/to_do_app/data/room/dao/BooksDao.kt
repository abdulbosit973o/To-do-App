package uz.abdulbosit.apps.to_do_app.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.abdulbosit.apps.to_do_app.data.room.entity.BookEntity


@Dao
interface BooksDao {
    @Query("SELECT * FROM ToDoData")
    fun getAllBooksFromLocal(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(data: BookEntity)

//    @Query("Select id From ToDoData where bookPath = :bookPath")
//    fun isHasBook(bookPath: String): Boolean
//
//    @Query("Select * From ToDoData  Where bookPath = :bookPath limit 1")
//    fun getBooksByDocID(bookPath: String): BookEntity

    @Query("DELETE FROM ToDoData WHERE id = :id")
    fun deleteToDo(id:Long)


    @Query("DELETE FROM ToDoData")
    fun nukeTable()

}