package com.celestial.progress.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.celestial.progress.R
import com.celestial.progress.ui.adapter.ItemAdapter
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentArchiveBinding
import com.celestial.progress.others.Utils.createDialogWithYesNo
import com.celestial.progress.ui.CounterViewModel
import com.google.firebase.installations.Utils
import kotlinx.coroutines.launch


class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null

    private val binding get() = _binding!!

    lateinit var adapter: ItemAdapter<ItemAdapter<*>.ItemViewHolder>

    lateinit var viewModel: CounterViewModel

    lateinit var archiveImgv: ImageView

    lateinit var archiveTv: TextView

    private val TAG = ArchiveFragment::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        val view = binding.root

        initUI()

        setUpListener()

        setUpViewModel()

        observeData()

        return view
    }

    private fun observeData() {
        viewModel.readArchiveCounters().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            backgroundImageTrigger(it)
            Log.d(TAG,"Archive List Size:${it.size}")
        })
    }

    private fun backgroundImageTrigger(list: List<Counter>) {

        Log.d(TAG,"Adapter List Size ${adapter.currentList.size}")

        if (list.isEmpty()) {
            archiveImgv.visibility = View.VISIBLE
            archiveTv.visibility = View.VISIBLE
        } else {
            archiveImgv.visibility = View.GONE
            archiveTv.visibility = View.GONE
        }
    }

    private fun initUI() {

        archiveImgv = binding.imgvArchiveIcon
        archiveTv = binding.tvArchiveText

        val toolbar = binding.toolbar2
        toolbar.setNavigationOnClickListener {
            goBack()
        }

        adapter = ItemAdapter(null, itemMenuShow, null, ItemAdapter.ItemViewHolder::class,isArchiveFragment = true)
        val rcyView = binding.archiveRcy
        rcyView.adapter = adapter

    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun setUpListener() {

    }

    fun goBack() {
        NavHostFragment.findNavController(this@ArchiveFragment)
                .popBackStack(R.id.dashboardFragment, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ArchiveFragment()
    }

    val itemMenuShow: (Counter, View) -> Unit = { c, v ->
        createPopUpMenuAndShow(v, c)
    }

    fun createPopUpMenuAndShow(v: View, c: Counter) {
        val popupMenu = PopupMenu(requireActivity(), v)
        popupMenu.apply {
            menuInflater.inflate(R.menu.archive_menu, popupMenu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_unarchive -> {
                        lifecycleScope.launch {
                            var counter = c
                            counter.isArchived = false
                            viewModel.updateCounter(counter)
                        }
                    }
                    R.id.item_delete -> {
                       createDialogWithYesNo(requireContext(),"Attention!","Do you want to delete this progress?") {
                           lifecycleScope.launch {
                               viewModel.deleteCounter(c)
                           }
                       }


                    }
                }
                true

            }
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}