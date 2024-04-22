package uz.abdulbosit.apps.to_do_app.data.model

import java.util.UUID

data class ToDoUIData(
    val id:Long,
    val todo:String,
    val date:String,
    val time:String,
    val isFinished:Int,
    val jobId:UUID
)
