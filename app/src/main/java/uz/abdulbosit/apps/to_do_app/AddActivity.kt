package uz.abdulbosit.apps.to_do_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import uz.abdulbosit.apps.to_do_app.data.model.ToDoData
import uz.abdulbosit.apps.to_do_app.domain.AppRepositoryImpl
import uz.abdulbosit.apps.to_do_app.utils.setDialogConfigurations
import uz.abdulbosit.apps.to_do_app.work.NotifyWork
import uz.abdulbosit.apps.to_do_app.databinding.ActivityAddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class AddActivity : AppCompatActivity(R.layout.activity_add) {
    private val binding by viewBinding(ActivityAddBinding::bind)
    private lateinit var dialog: Dialog
    private var year = 0
    private var month = 0
    private var dayOfMonth = 0
    private var hour = 0
    private var minute = 0
    private val REQ_CODE_SPEECH_INPUT = 100
    private var tts: TextToSpeech? = null
    private lateinit var checkNotificationPermission: ActivityResultLauncher<String>
    private var isPermission = false
    private val repositoryImpl = AppRepositoryImpl.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = Dialog(this)

        checkNotificationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermission = isGranted
        }
        checkPermission()

        binding.backMarketBtn.setOnClickListener { finish() }
        binding.datePicker.setOnClickListener {
            openDateDialog()
        }
        binding.timePicker.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                openTimeDialog()
            }
        }
        userInterface()
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_name))

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this, "Sorry! Your device doesn\\'t support speech input",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val message = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                updateResults(message!![0])
            }
        }
    }

    private fun updateResults(s: String) {
        var text = binding.etAddress.text.toString()
        text += s
        binding.etAddress.setText(text)
    }

    private fun ConvertTextToSpeech(string: String) {
        if ("" == string) {
            tts?.speak(string, TextToSpeech.QUEUE_ADD, null, "0.45")
        } else tts?.speak(string, TextToSpeech.QUEUE_ADD, null, "0.45")
    }

    override fun onPause() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onPause()
    }

    private fun userInterface() {
        binding.voice.setOnClickListener {
            promptSpeechInput()
        }


        binding.btnAccept.setOnClickListener {
            if (binding.etAddress.text.toString().isEmpty()) {
                Snackbar.make(
                    binding.root,
                    "Enter task first",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (isPermission) {
                val customCalendar = Calendar.getInstance()
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
                    val data = Data.Builder().putInt(NotifyWork.NOTIFICATION_ID, 0).build()
                    val delay = customTime - currentTime
                    val tag = scheduleNotification(delay, data)

                    val titleNotificationSchedule = getString(R.string.notification_schedule_title)
                    val patternNotificationSchedule =
                        getString(R.string.notification_schedule_pattern)
                    Snackbar.make(
                        binding.root,
                        titleNotificationSchedule + SimpleDateFormat(
                            patternNotificationSchedule, Locale.getDefault()
                        ).format(customCalendar.time).toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                    repositoryImpl.addTodoToRoom(
                        ToDoData(
                            todo = binding.etAddress.text.toString(),
                            date = "${pad(dayOfMonth)}.${pad(month)}.${pad(year)}",
                            time = "${pad(hour)}:${pad(minute)}",
                            isFinished = 0,
                            jobId = tag
                        )
                    )
                    finish()
                } else {
                    val errorNotificationSchedule = getString(R.string.notification_schedule_error)
                    Snackbar.make(
                        binding.root,
                        errorNotificationSchedule,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun scheduleNotification(delay: Long, data: Data):UUID {
        val tag = UUID.randomUUID()
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .addTag(tag.toString())
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val id = notificationWork.id

        // mana bu yerda work ning jobId sini olib room ga saqlayabman

        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            NotifyWork.NOTIFICATION_WORK,
            ExistingWorkPolicy.APPEND, notificationWork
        ).enqueue()

        return tag
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false

                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openDateDialog() {
        dialog.setContentView(R.layout.dialog_date)

        dialog.findViewById<AppCompatButton>(R.id.btn_save).setOnClickListener {
            cancelDialog()
            val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
            year = datePicker.year
            month = datePicker.month
            dayOfMonth = datePicker.dayOfMonth
            binding.datePicker.text =
                "${datePicker.year}.${pad(datePicker.month)}.${pad(datePicker.dayOfMonth)}"
        }
        dialog.findViewById<AppCompatButton>(R.id.btn_close).setOnClickListener {
            cancelDialog()
        }
        dialog.setDialogConfigurations(true, Gravity.CENTER)


    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun openTimeDialog() {
        dialog.setContentView(R.layout.dialog_time)

        dialog.findViewById<AppCompatButton>(R.id.btn_save).setOnClickListener {
            cancelDialog()
            val datePicker = dialog.findViewById<TimePicker>(R.id.timePicker)
            hour = datePicker.hour
            minute = datePicker.minute
            binding.timePicker.text = "${pad(datePicker.hour)}:${pad(datePicker.minute)}"
        }
        dialog.findViewById<AppCompatButton>(R.id.btn_close).setOnClickListener {
            cancelDialog()
        }
        dialog.setDialogConfigurations(true, Gravity.CENTER)
    }

    private fun pad(c: Int): String {
        return if (c >= 10)
            c.toString();
        else "0$c";
    }

    private fun cancelDialog() = dialog.dismiss()

}