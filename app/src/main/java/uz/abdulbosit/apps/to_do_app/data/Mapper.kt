package ru.ifr0z.notify.data

import uz.abdulbosit.apps.to_do_app.data.model.ToDoData
import uz.abdulbosit.apps.to_do_app.data.model.ToDoUIData
import uz.abdulbosit.apps.to_do_app.data.room.entity.BookEntity

object Mapper {
    fun BookEntity.toToDoUIData(): ToDoUIData =
        ToDoUIData(
            id = this.id,
            todo = this.todo,
            date = this.date,
            time = this.time,
            isFinished = this.isFinished,
            jobId = this.jobId
        )

    fun ToDoData.toBookEntity(): BookEntity =
        BookEntity(
            id = 0,
            todo = this.todo,
            date = this.date,
            time = this.time,
            isFinished = this.isFinished,
            jobId = this.jobId
        )

    fun ToDoData.toBookEntity(id:Long): BookEntity =
        BookEntity(
            id = id,
            todo = this.todo,
            date = this.date,
            time = this.time,
            isFinished = this.isFinished,
            jobId = this.jobId
        )

    fun ToDoUIData.toBookEntity(i: Int): BookEntity {
        return BookEntity(
            id = id,
            todo = this.todo,
            date = this.date,
            time = this.time,
            isFinished = i,
            jobId = this.jobId
        )
    }
}