package uz.abdulbosit.apps.to_do_app.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ToDoData")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) var id:Long,
    val todo:String,
    val date:String,
    val time:String,
    val isFinished:Int,
    val jobId:UUID
)