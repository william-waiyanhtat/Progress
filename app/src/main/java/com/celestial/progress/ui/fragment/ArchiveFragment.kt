package com.celestial.progress.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.celestial.progress.R
import com.celestial.progress.ui.adapter.ItemAdapter
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentArchiveBinding
import com.celestial.progress.ui.CounterViewModel
import kotlinx.coroutines.launch


class ArchiveFragment : Fragment() {

    lateinit var binding: FragmentArchiveBinding

    lateinit var adapter: ItemAdapter

    lateinit var viewModel: CounterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentArchiveBinding.inflate(inflater, container, false)
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
        })
    }

    private fun initUI() {

        val toolbar = binding.toolbar2
        toolbar.setNavigationOnClickListener {
            goBack()
        }

        adapter = ItemAdapter(null, itemMenuShow)
        val rcyView = binding.archiveRcy
        rcyView.adapter = adapter

    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
    }

    private fun setUpListener() {
//        TODO("Not yet implemented")
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

    val itemMenuShow: (Counter, View) -> Unit ={ c, v ->
        createPopUpMenuAndShow(v,c)
    }

    fun createPopUpMenuAndShow(v: View, c: Counter){
        val popupMenu = PopupMenu(requireActivity(), v)
        popupMenu.apply {
            menuInflater.inflate(R.menu.archive_menu, popupMenu.menu)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.item_unarchive -> {
                        lifecycleScope.launch {
                            var counter = c
                            counter.isArchived = false
                            viewModel.updateCounter(counter)

                        }
                    }
                    R.id.item_delete -> {
                        lifecycleScope.launch {
                            viewModel.deleteCounter(c)
                        }
                    }
                }
                true

            }
            show()
        }

    }
}