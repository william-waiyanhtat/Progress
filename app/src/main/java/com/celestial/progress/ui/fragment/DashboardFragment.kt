package com.celestial.progress.ui.fragment

import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.celestial.progress.MainActivity
import com.celestial.progress.R
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentDashboardBinding
import com.celestial.progress.ui.CounterViewModel
import com.celestial.progress.ui.adapter.ItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


private val TAG = DashboardFragment::class.java.name

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    lateinit var viewModel: CounterViewModel

    lateinit var adapter: ItemAdapter<ItemAdapter<*>.ItemViewHolder>

    var rcyState: Parcelable? = null

    var bufferList = ArrayList<Counter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        if(bufferList.isNotEmpty())
            bufferList.clear()
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root
        val toolbar = binding.toolbarCreate

        setListenerToolbar(toolbar)

        toolbar.inflateMenu(R.menu.btm_menu)

        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        adapter = ItemAdapter(expandCollapse, itemMenuShow, ItemAdapter.ItemViewHolder::class)
        binding.counterRcy.adapter = adapter
        binding.counterRcy.layoutManager = LinearLayoutManager(context)
        val itemTouchHelper = ItemTouchHelper(itemTouchSimpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.counterRcy)

        setListener()
        observeData()
        Log.d(TAG, "On Create View")

        return view
    }

    private fun setListenerToolbar(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.Add -> {
                    if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                        val extras = FragmentNavigator.Extras.Builder()
                                .addSharedElement(
                                        binding.toolbarCreate, "fabBtn"
                                ).build()

                        findNavController().navigate(R.id.navigateToCreateFragment, null, null, extras)
                    }
                }
                R.id.archive -> {
                    if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
//                        val extras = FragmentNavigator.Extras.Builder()
//                            .addSharedElement(
//                                binding.fab, "fabBtn"
//                            ).build()
                        findNavController().navigate(R.id.action_dashboardFragment_to_archiveFragment)
                        // findNavController().navigate(R.id.navigateToCreateFragment, null, null, extras)
                    }
                }
                R.id.setting -> {
                    if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
//                        val extras = FragmentNavigator.Extras.Builder()
//                                .addSharedElement(
//                                        binding.toolbarCreate, "fabBtn"
//                                ).build()

                        findNavController().navigate(R.id.dashToSetting)
                    }
                }
            }

            true

        }

    }

    private fun observeData() {
        viewModel?.let {
            it.readAllCounters().observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "Data get ${it.size}")
//                binding.counterRcy.adapter  = ItemAdapter(expandCollapse, itemMenuShow, ItemAdapter.ItemViewHolder::class)
                adapter.submitList(it)

                Log.d(TAG, "Observed List: **************")
                for(c in it){
                    Log.d(TAG, "Observed List: ${c.title} order: ${c.order}")
                }
            })

        }
    }

    fun setListener() {
        binding.fab.setOnClickListener {
            (activity as MainActivity).showHideAppBar(false)

//            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
//                val extras = FragmentNavigator.Extras.Builder()
//                    .addSharedElement(
//                        binding.fab, "fabBtn"
//                    ).build()
//
//               findNavController().navigate(R.id.navigateToCreateFragment, null, null, extras)
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DashboardFragment()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "On Pause")
        lifecycleScope.launch {
            viewModel.insertAll(bufferList)

        }
        rcyState = binding.counterRcy.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On Resume")

        // binding.counterRcy.layoutManager?.onRestoreInstanceState(rcyState)
    }

    val itemTouchSimpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
    ) {
        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.layoutPosition
            val to = target.layoutPosition
            Collections.swap(bufferList,from,to)
//            val counterFrom = (recyclerView.adapter as ItemAdapter<*>).currentList[from]
//            val counterTo = (recyclerView.adapter as ItemAdapter<*>).currentList[to]
//            counterFrom.order = to
//            counterTo.order = from

//            if (from < to) {
//                for (i in from until to) {
//                    Collections.swap(bufferList, i, i + 1)
//                }
//            } else {
//                for (i in from downTo to + 1) {
//                    Collections.swap(bufferList, i, i - 1)
//                }
//            }
         //   Collections.swap(bufferList,from,to)


//            lifecycleScope.launch {
//                withContext(Dispatchers.IO){
//                    Log.d(TAG, "Thread in Move: ${Looper.getMainLooper().isCurrentThread}")
//                    viewModel.updateCounter(counterFrom)
//                    viewModel.updateCounter(counterTo)
//
//                 }
//                   }


                recyclerView.adapter?.notifyItemMoved(from, to)

            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {


            when(actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    if (adapter != null) {
                        if(bufferList.isEmpty())
                            bufferList.addAll(adapter.currentList)
                        else if(adapter.currentList.size>bufferList.size){

                        }
                    }

                    Log.d("DragTest", "Start to drag: $actionState")
                }
                ItemTouchHelper.ACTION_STATE_SWIPE ->
                    Log.d("DragTest", "Start to swipe: $actionState")
                ItemTouchHelper.ACTION_STATE_IDLE -> {
                    Log.d("DragTest","*****************")

                    for(i in 0 until bufferList.size){
                        bufferList[i].order = i
                        Log.d("DragTest","CurrentList ${adapter.currentList[i].title} Order:${adapter.currentList[i].order}")
                        Log.d("DragTest","Counter: ${bufferList[i].title} Order: ${ bufferList[i].order}")
                    }
                    Log.d("DragTest","*****************")
                    Log.d("DragTest", "End action: $actionState")
                }
            }
        }




        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }

    }

    val expandCollapse: (Counter) -> Unit = {
        Log.d(TAG, "EXPAND COLLAPSE CALLED")
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO){
                Log.d(TAG, "MainThread: ${Looper.getMainLooper().isCurrentThread}")
                viewModel.updateCounter(it)
            }
        }
    }

    val itemMenuShow: (Counter, View) -> Unit = { c, v ->
        Log.d(TAG, "Item Menu : ${c.title}")
        createPopUpMenuAndShow(v, c)
    }


    fun createPopUpMenuAndShow(v: View, c: Counter) {
        val popupMenu = PopupMenu(requireActivity(), v)
        popupMenu.apply {
            menuInflater.inflate(R.menu.menu_item, popupMenu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_edit -> {
                        TODO("To implement edit counter feature")
                    }
                    R.id.item_archive -> {
                        lifecycleScope.launch {
                            var counter = c
                            counter.isArchived = true
                            viewModel.updateCounter(counter)

                        }
                    }
                }
                true

            }
            show()
        }

    }

    override fun onStop() {
        super.onStop()
    }



}