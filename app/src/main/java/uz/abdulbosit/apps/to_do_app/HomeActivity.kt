package uz.abdulbosit.apps.to_do_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import uz.abdulbosit.apps.to_do_app.databinding.ActivityHomeBinding
import uz.abdulbosit.apps.to_do_app.domain.AppRepositoryImpl
import uz.abdulbosit.apps.to_do_app.presentation.ExploreAdapter
import uz.abdulbosit.apps.to_do_app.utils.gone
import uz.abdulbosit.apps.to_do_app.utils.visible
import uz.abdulbosit.apps.to_do_app.work.NotifyWork

class HomeActivity() : AppCompatActivity(R.layout.activity_home) {
    private val adapter = ExploreAdapter()
    private val binding by viewBinding(ActivityHomeBinding::bind)
    private val repositoryImpl = AppRepositoryImpl.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addMarketBtn.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
        adapter.setClickBookListener {
            NotifyWork.cancelJobsByTag(it.jobId.toString(), this)
            repositoryImpl.editToRoom(it)
            Snackbar.make(
                binding.root,
                "Cancelling succeed",
                Snackbar.LENGTH_LONG
            ).show()
            onResume()
        }
        adapter.setClickItemListener {
            val intent = Intent(this@HomeActivity, EditActivity::class.java)
            intent.putExtra("date", it.date)
            intent.putExtra("time", it.time)
            intent.putExtra("todo", it.todo)
            intent.putExtra("jobId", it.jobId.toString())
            intent.putExtra("id", it.id.toString())
            intent.putExtra("isFinished", it.isFinished.toString())
            startActivity(intent)
        }
        binding.appBarFinish.setOnClickListener {
            val intent = Intent(this@HomeActivity, FinishActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val list = repositoryImpl.getAllBooksFromRoom()
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