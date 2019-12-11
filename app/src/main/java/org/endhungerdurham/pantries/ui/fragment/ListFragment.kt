package org.endhungerdurham.pantries.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.endhungerdurham.pantries.backend.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.adapter.MyItemRecyclerViewAdapter
import org.endhungerdurham.pantries.ui.viewmodel.NetworkState
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val REFRESH_ANIMATION_DELAY: Long = 1000

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ListFragment.OnListFragmentInteractionListener] interface.
 */
// TODO: Sort by distance
class ListFragment : androidx.fragment.app.Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var model: PantriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(requireActivity()).get(PantriesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)


        val recyclerView = view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)?.apply {
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(requireContext(), VERTICAL))
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        model.pantries.observe(viewLifecycleOwner, Observer<List<Pantry>> { pantries ->
            recyclerView?.adapter = MyItemRecyclerViewAdapter(pantries ?: emptyList(), listener)
        })

        view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.list_refresh)?.let {
            it.setOnRefreshListener {
                model.reloadPantries()
                it.isRefreshing = false
            }
        }

        model.networkState.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                NetworkState.SUCCESS -> setRefreshing(false)
                NetworkState.LOADING -> setRefreshing(true)
                NetworkState.FAILURE -> {
                    Toast.makeText(view?.context, requireContext().getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                    setRefreshing(false)
                }
                else -> {}
            }
        })

        return view
    }

    override fun onStart() {
        super.onStart()

        requireActivity().findViewById<TabLayout>(R.id.sliding_tabs).visibility = View.VISIBLE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setRefreshing(isRefreshing: Boolean) {
        view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.list_refresh)?.let {
            when (isRefreshing) {
                false -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(REFRESH_ANIMATION_DELAY)
                        it.isRefreshing = false
                    }
                }
                true -> it.isRefreshing = true
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Pantry)
    }
}
