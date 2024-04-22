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
            //
            Snackbar.make(
                binding.root,
                "Task canceling is not working at the moment. This is cancelling only room",
                Snackbar.LENGTH_LONG
            ).show()
            NotifyWork.cancelJobsByTag(it.jobId.toString(), this)
            repositoryImpl.deleteBookInRoom(it.id)
            onResume()
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
        adapter.submitList(list)
    }


}