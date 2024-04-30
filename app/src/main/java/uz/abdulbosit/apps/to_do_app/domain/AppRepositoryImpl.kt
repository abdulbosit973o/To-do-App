package uz.abdulbosit.apps.to_do_app.domain

import ru.ifr0z.notify.data.Mapper.toBookEntity
import ru.ifr0z.notify.data.Mapper.toToDoUIData
import uz.abdulbosit.apps.to_do_app.data.model.ToDoData
import uz.abdulbosit.apps.to_do_app.data.model.ToDoUIData
import ru.ifr0z.notify.data.room.AppDatabase
import ru.ifr0z.notify.data.source.local.impl.MySharedPrefImpl


class AppRepositoryImpl : AppRepository {

    private val shared = MySharedPrefImpl.getInstance()
    private val booksDao = AppDatabase.getInstance().getBooksDao()

    companion object {
        private lateinit var instance: AppRepository
        fun getInstance(): AppRepository {
            if (!(Companion::instance.isInitialized)) {
                instance = AppRepositoryImpl()
            }
            return instance
        }
    }

    override fun getAllBooksFromRoom(): List<ToDoUIData> {
        val ls = booksDao.getAllBooksFromLocal()
        return if (ls.isNotEmpty()) {
            val list = ArrayList<ToDoUIData>()
            ls.forEach {
                list.add(it.toToDoUIData())
            }
            list
        } else {
            emptyList()
        }
    }

    override fun getAllBooksFromRoomFinish(): List<ToDoUIData> {
        val ls = booksDao.getAllBooksFromLocalFinished()
        return if (ls.isNotEmpty()) {
            val list = ArrayList<ToDoUIData>()
            ls.forEach {
                list.add(it.toToDoUIData())
            }
            list
        } else {
            emptyList()
        }
    }


    override fun deleteBookInRoom(id: Long) {
        booksDao.deleteToDo(id)
    }

    override fun addTodoToRoom(toDoData: ToDoData) {
        booksDao.insertBooks(toDoData.toBookEntity())
    }

    override fun editToRoom(taskID: Long, toDoData: ToDoData) {
        booksDao.updateBooks(toDoData.toBookEntity(taskID))
    }

    override fun editToRoom(taskID: ToDoUIData) {
        booksDao.updateBooks(taskID.toBookEntity(if (taskID.isFinished == 1) 0 else 1))
    }
}
