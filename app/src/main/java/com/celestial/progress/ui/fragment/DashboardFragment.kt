package com.celestial.progress.ui.fragment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.celestial.progress.data.adapter.ItemAdapter
import com.celestial.progress.data.model.Counter
import com.celestial.progress.databinding.FragmentDashboardBinding
import com.celestial.progress.ui.CounterViewModel
import kotlinx.coroutines.launch


private val TAG = DashboardFragment::class.java.name

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? =null

    private val binding get() = _binding!!

    lateinit var viewModel: CounterViewModel

    lateinit var adapter: ItemAdapter

    var rcyState: Parcelable? = null

    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root


        viewModel = ViewModelProvider(requireActivity())[CounterViewModel::class.java]
        adapter = ItemAdapter(expandCollapse, itemMenuShow)
        binding.counterRcy.adapter = adapter
        binding.counterRcy.layoutManager = LinearLayoutManager(context)
        val itemTouchHelper = ItemTouchHelper(itemTouchSimpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.counterRcy)

        setListener()
        observeData()
        Log.d(TAG, "On Create View")

        return view
    }

    private fun observeData() {
      viewModel?.let {
          it.readAllCounters().observe(viewLifecycleOwner, Observer {
              Log.d(TAG, "Data get ${it.size}")

              adapter.submitList(it)
              binding.counterRcy.layoutManager?.onRestoreInstanceState(rcyState)
          })

      }
    }

    fun setListener(){
        binding.textView.setOnClickListener{
            (activity as MainActivity).callback()
            Log.d(TAG, "Click on Listener")
        }

        binding.fab.setOnClickListener {
            (activity as MainActivity).showHideAppBar(false)

            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                val extras = FragmentNavigator.Extras.Builder()
                    .addSharedElement(
                        binding.fab, "fabBtn"
                    ).build()

               findNavController().navigate(R.id.navigateToCreateFragment, null, null, extras)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
        rcyState = binding.counterRcy.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On Resume")
       // binding.counterRcy.layoutManager?.onRestoreInstanceState(rcyState)
    }

    val itemTouchSimpleCallback = object: ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        0
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition

            val counterFrom = (recyclerView.adapter as ItemAdapter).currentList[from]
            val counterTo = (recyclerView.adapter as ItemAdapter).currentList[to]
            counterFrom.order = to
            counterTo.order = from


            lifecycleScope.launch {
                viewModel.updateCounter(counterFrom)
                viewModel.updateCounter(counterTo)
            }


        //    recyclerView.adapter?.notifyItemMoved(from,to)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }

    }

    val expandCollapse: (Counter) -> Unit = {
        
        Log.d(TAG,"EXPAND COLLAPSE CALLED")
        lifecycleScope.launch {
            viewModel.updateCounter(it)
        }
    }

    val itemMenuShow: (Counter, View) -> Unit ={ c,v ->
        Log.d(TAG,"Item Menu : ${c.title}")
        createPopUpMenuAndShow(v)
    }


    fun createPopUpMenuAndShow(v: View){
        val popupMenu = PopupMenu(requireActivity(),v)
        popupMenu.apply {
            menuInflater.inflate(R.menu.menu_item,popupMenu.menu)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.item_edit -> {}
                    R.id.item_archive ->{}
                }
                true

            }
            show()
        }

    }
}