package com.celestial.progress.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
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
import com.celestial.progress.others.NotificationHelper
import com.celestial.progress.others.Utils
import com.celestial.progress.ui.CounterViewModel
import com.celestial.progress.ui.adapter.ItemAdapter
import kotlinx.coroutines.*
import java.util.*


private val TAG = DashboardFragment::class.java.name

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    lateinit var viewModel: CounterViewModel

    lateinit var adapter: ItemAdapter<ItemAdapter<*>.ItemViewHolder>

    var rcyState: Parcelable? = null

    var bufferList = ArrayList<Counter>()

    var notificationIssueList = ArrayList<Counter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        if (bufferList.isNotEmpty())
            bufferList.clear()

        if (notificationIssueList.isNotEmpty())
            notificationIssueList.clear()

        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root
        val toolbar = binding.toolbarCreate

        (activity as MainActivity).showHideToolbar(false)

        setListenerToolbar(toolbar)

        toolbar.inflateMenu(R.menu.btm_menu)

        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        adapter = ItemAdapter(expandCollapse, itemMenuShow, notificationCb, ItemAdapter.ItemViewHolder::class)
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

                        // val bundle = bundleOf("isCreate" to false)

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
                showHideBackgroundImage(it.isEmpty())
                adapter.submitList(it)
                checkAndCancelInvalidProgress(it)
                for (i in it) {
                    checkCreateAndCancelNotification(i)
                }


            })

        }
    }

    private fun checkAndCancelInvalidProgress(it: List<Counter>?) {
        val notificationArray = NotificationHelper.getNotificationList(requireContext())
        if (it != null) {
            for (c in it) {
                for (i in notificationArray) {
                    if (i.id == c.id)
                        break
                }
                NotificationHelper.cancelNotification(requireContext(), c)
            }
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
        _binding = null
        super.onDestroyView()

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

            for (i in notificationIssueList) {
                viewModel.updateCounterForNotificationById(i.id!!, i.requiredNotification)
            }


        }
        rcyState = binding.counterRcy.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On Resume")
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
            Collections.swap(bufferList, from, to)
            recyclerView.adapter?.notifyItemMoved(from, to)

            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    if (adapter != null) {
                        if (bufferList.isEmpty())
                            bufferList.addAll(adapter.currentList)
                        else if (adapter.currentList.size > bufferList.size) {

                        }
                    }

                    Log.d("DragTest", "Start to drag: $actionState")
                }
                ItemTouchHelper.ACTION_STATE_SWIPE ->
                    Log.d("DragTest", "Start to swipe: $actionState")
                ItemTouchHelper.ACTION_STATE_IDLE -> {
                    Log.d("DragTest", "*****************")

                    for (i in 0 until bufferList.size) {
                        bufferList[i].order = i
                        Log.d("DragTest", "CurrentList ${adapter.currentList[i].title} Order:${adapter.currentList[i].order}")
                        Log.d("DragTest", "Counter: ${bufferList[i].title} Order: ${bufferList[i].order}")
                    }
                    Log.d("DragTest", "*****************")
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
            withContext(Dispatchers.IO) {
                Log.d(TAG, "MainThread: ${Looper.getMainLooper().isCurrentThread}")
                viewModel.updateCounter(it)


            }
        }
    }

    val itemMenuShow: (Counter, View) -> Unit = { c, v ->
        Log.d(TAG, "Item Menu : ${c.title}")
        createPopUpMenuAndShow(v, c)
    }

    val notificationCb: (Counter) -> Unit = {
        if (!notificationIssueList.contains(it)) {
            notificationIssueList.add(it)
        }

        for (n in notificationIssueList) {
            Log.d(TAG, "NotiList: ${n.id} name: ${n.title} isNotify: ${n.requiredNotification}")
        }

    }


    fun createPopUpMenuAndShow(v: View, c: Counter) {
        val popupMenu = PopupMenu(requireActivity(), v)
        popupMenu.apply {
            menuInflater.inflate(R.menu.menu_item, popupMenu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_edit -> {
                        if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                            val bundle = bundleOf("isCreate" to false)
                            viewModel.editCounter = c

                            val extras = FragmentNavigator.Extras.Builder()
                                    .addSharedElement(
                                            binding.toolbarCreate, "fabBtn"
                                    ).build()

                            findNavController().navigate(R.id.navigateToCreateFragment, bundle, null, extras)
                        }
                    }
                    R.id.item_archive -> {
                        Utils.createDialogWithYesNo(requireContext(), "", "Archiving progress will turn-of notification and widget," +
                                " if this progress has related notification and widget. Are you sure?") {

                            lifecycleScope.launch {
                                var counter = c
                                counter.isArchived = true
                                if (counter.requiredNotification) {
                                    NotificationHelper.cancelNotification(requireContext(), counter)
                                    counter.requiredNotification = false
                                }
                                viewModel.updateCounter(counter)

                            }
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

    private fun checkCreateAndCancelNotification(model: Counter) {


        if (model.requiredNotification) {
            //binding.swNoti.isChecked = true
            //   if(!NotificationHelper.checkNotification(requireContext(), model)){
            NotificationHelper.createNotification(requireContext(), model)
            //    }
        } else {
            // binding.swNoti.isChecked = false
            if (NotificationHelper.checkNotification(requireContext(), model)) {
                NotificationHelper.cancelNotification(requireContext(), model)
            }
        }
    }

    private fun showHideBackgroundImage(isVisible: Boolean){
        if(isVisible){
            binding.bgGroup.visibility = View.VISIBLE
        }else{
            binding.bgGroup.visibility = View.GONE
        }
    }


}