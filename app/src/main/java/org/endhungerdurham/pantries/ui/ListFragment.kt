package org.endhungerdurham.pantries.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
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

private const val REFRESH_ANIMATION_DELAY: Long = 1000

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

        view?.findViewById<RecyclerView>(R.id.list)?.apply {
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
            layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        requireActivity().findViewById<TabLayout>(R.id.sliding_tabs).visibility = View.VISIBLE

        val swipeContainer = view?.findViewById<SwipeRefreshLayout>(R.id.list_refresh)
        swipeContainer?.setOnRefreshListener {
            model.reloadPantries()
            swipeContainer.isRefreshing = false
        }

        model.pantries.observe(this, Observer<List<Pantry>> { pantries ->
            val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
            recyclerView?.adapter = MyItemRecyclerViewAdapter(pantries ?: emptyList(), listener)
        })

        model.networkState.observe(this, Observer { result ->
            when (result) {
                NetworkState.SUCCESS -> setRefreshing(false)
                NetworkState.LOADING -> setRefreshing(true)
                NetworkState.FAILURE -> {
                    Toast.makeText(view?.context, requireContext().getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                    setRefreshing(false)
                }
            }
        })
    }

    private fun setRefreshing(isRefreshing: Boolean) {
        val swipeContainer = view?.findViewById(R.id.list_refresh) as? SwipeRefreshLayout
        when (isRefreshing) {
            false -> {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(REFRESH_ANIMATION_DELAY)
                    swipeContainer?.isRefreshing = false
                }
            }
            true -> swipeContainer?.isRefreshing = true
        }
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
