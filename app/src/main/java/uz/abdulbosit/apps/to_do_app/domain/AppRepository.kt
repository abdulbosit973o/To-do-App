package uz.abdulbosit.apps.to_do_app.domain

import uz.abdulbosit.apps.to_do_app.data.model.ToDoData
import uz.abdulbosit.apps.to_do_app.data.model.ToDoUIData

interface AppRepository {
    fun getAllBooksFromRoom(): List<ToDoUIData>
    fun getAllBooksFromRoomFinish(): List<ToDoUIData>
    fun deleteBookInRoom(id: Long)
    fun addTodoToRoom(toDoData: ToDoData)
    fun editToRoom(taskID: Long, toDoData: ToDoData)
    fun editToRoom(taskID: ToDoUIData)
}