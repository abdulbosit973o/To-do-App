package uz.abdulbosit.apps.to_do_app

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import uz.abdulbosit.apps.to_do_app.databinding.ActivityFinishBinding
import uz.abdulbosit.apps.to_do_app.databinding.ActivityHomeBinding
import uz.abdulbosit.apps.to_do_app.domain.AppRepositoryImpl
import uz.abdulbosit.apps.to_do_app.presentation.ExploreAdapter
import uz.abdulbosit.apps.to_do_app.presentation.FinishedAdapter
import uz.abdulbosit.apps.to_do_app.utils.gone
import uz.abdulbosit.apps.to_do_app.utils.myLog
import uz.abdulbosit.apps.to_do_app.utils.visible
import uz.abdulbosit.apps.to_do_app.work.NotifyWork
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

class FinishActivity : AppCompatActivity() {
    private val adapter = FinishedAdapter()
    private lateinit var binding: ActivityFinishBinding
    private var tag: String = ""
    private val repositoryImpl = AppRepositoryImpl.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setClickBookListener {
            tag = it.jobId.toString()
            val customCalendar = Calendar.getInstance()
            val year = it.date.substring(6).toInt()
            val month = it.date.substring(3, 5).toInt()
            val dayOfMonth = it.date.substring(0, 2).toInt()
            val hour = it.time.substring(0, 2).toInt()
            val minute = it.time.substring(3).toInt()
            customCalendar.set(
                year,
                month,
                dayOfMonth,
                hour,
                minute, 0
            )
            val customTime = customCalendar.timeInMillis
            val currentTime = System.currentTimeMillis()
            if (customTime > currentTime) {
                "Success Time Katta".myLog()
                val data = Data.Builder().putInt(NotifyWork.NOTIFICATION_ID, 0).build()
                val delay = customTime - currentTime
                scheduleNotification(delay, data)
            }
            repositoryImpl.editToRoom(it)
            Snackbar.make(
                binding.root,
                "Returning succeed",
                Snackbar.LENGTH_LONG
            ).show()
            onResume()
        }
        binding.appBarFinish.setOnClickListener {
            finish()
        }
        adapter.setClickItemListener {
            val intent = Intent(this@FinishActivity, EditActivity::class.java)
            intent.putExtra("date", it.date)
            intent.putExtra("time", it.time)
            intent.putExtra("todo", it.todo)
            intent.putExtra("jobId", it.jobId.toString())
            intent.putExtra("id", it.id.toString())
            intent.putExtra("isFinished", it.isFinished.toString())
            startActivity(intent)
        }
    }

    private fun scheduleNotification(delay: Long, data: Data): UUID {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .addTag(tag)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        "scheduleNotification Tag = $tag".myLog()

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            tag,
            ExistingWorkPolicy.REPLACE, notificationWork
        ).enqueue()

        return UUID.fromString(tag)
    }

    override fun onResume() {
        super.onResume()

        val list = repositoryImpl.getAllBooksFromRoomFinish()
        if (list.isEmpty()) {
            binding.imgPlaceholder.visible()
            binding.tv1Placeholder.visible()
            binding.tv2Placeholder.visible()
        } else {
            binding.imgPlaceholder.gone()
            binding.tv1Placeholder.gone()
            binding.tv2Placeholder.gone()
        }
        list.sortedBy {
            it.id
        }
        adapter.submitList(list)
    }
}