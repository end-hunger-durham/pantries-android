package org.endhungerdurham.pantries.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.adapter.MyItemRecyclerViewAdapter
import org.endhungerdurham.pantries.ui.viewmodel.NetworkState
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ListFragment.OnListFragmentInteractionListener] interface.
 */
// TODO: Sort by distance
class ListFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var model: PantriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(requireActivity()).get(PantriesViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val swipeContainer = view.findViewById(R.id.list_refresh) as? SwipeRefreshLayout
        swipeContainer?.setOnRefreshListener {
            model.reloadPantries()
            swipeContainer.isRefreshing = false
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)?.apply {
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
            layoutManager = LinearLayoutManager(context)
        }

        model.pantries.observe(this, Observer<List<Pantry>> { pantries ->
            recyclerView?.adapter = MyItemRecyclerViewAdapter(pantries ?: emptyList(), listener)
        })

        model.networkState.observe(this, Observer { result ->
            when (result) {
                NetworkState.SUCCESS -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        swipeContainer?.isRefreshing = false
                    }
                }
                NetworkState.LOADING -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        swipeContainer?.isRefreshing = true
                    }
                }
                NetworkState.FAILURE -> {
                    Toast.makeText(view.context, requireContext().getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        swipeContainer?.isRefreshing = false
                    }
                }
            }
        })

        return view
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
