package uz.abdulbosit.apps.to_do_app.presentation

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.abdulbosit.apps.to_do_app.data.model.ToDoUIData
import ru.ifr0z.notify.data.source.local.MySharedPref
import ru.ifr0z.notify.data.source.local.impl.MySharedPrefImpl
import uz.abdulbosit.apps.to_do_app.databinding.ItemLavozimBinding
import java.util.Calendar

class FinishedAdapter : ListAdapter<ToDoUIData, FinishedAdapter.ViewHolder>(diffUtil) {

    private lateinit var clickBookListener: ((ToDoUIData) -> Unit)
    private lateinit var clickItemListener: ((ToDoUIData) -> Unit)
    private val sharedPref: MySharedPref = MySharedPrefImpl.getInstance()

    private object diffUtil : DiffUtil.ItemCallback<ToDoUIData>() {
        override fun areItemsTheSame(oldItem: ToDoUIData, newItem: ToDoUIData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDoUIData, newItem: ToDoUIData): Boolean {
            return oldItem == newItem
        }

    }

    fun setClickBookListener(block: (ToDoUIData) -> Unit) {
        clickBookListener = block
    }
    fun setClickItemListener(block: (ToDoUIData) -> Unit) {
        clickItemListener = block
    }

    inner class ViewHolder(private val binding: ItemLavozimBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(book: ToDoUIData) {
            binding.apply {
                val customCalendar = Calendar.getInstance()
                val year = book.date.substring(6).toInt()
                val month = book.date.substring(3, 5).toInt()
                val dayOfMonth = book.date.substring(0, 2).toInt()
                val hour = book.time.substring(0, 2).toInt()
                val minute = book.time.substring(3).toInt()
                customCalendar.set(
                    year,
                    month,
                    dayOfMonth,
                    hour,
                    minute, 0
                )
                val customTime = customCalendar.timeInMillis
                val currentTime = System.currentTimeMillis()
                if (customTime < currentTime) {

                }
                else {
                    textDescription.setTextColor(Color.parseColor("#a5a5a5"))
                }

                when(book.isFinished) {
                    0-> {
                       binding.checkbox.isChecked = false
                    } else -> {
                    binding.checkbox.isChecked = true
                }
                }
                textName.text = book.todo
                textDescription.text = "${book.date}, ${book.time}"

                binding.checkbox.setOnClickListener {
                    checkbox.isChecked = true
                    clickBookListener.invoke(book)
                }
                binding.root.setOnClickListener {
                    clickItemListener.invoke(book)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLavozimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.animatsiya)

        holder.bind(getItem(position))
    }

}
