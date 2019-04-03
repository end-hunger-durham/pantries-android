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
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.adapter.MyItemRecyclerViewAdapter
import org.endhungerdurham.pantries.ui.viewmodel.NetworkState
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val KEY_SEARCH_QUERY = "search_query"

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ListFragment.OnListFragmentInteractionListener] interface.
 */
// TODO: Sort by distance
class ListFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var model: PantriesViewModel
    private var mSearchQuery: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(requireActivity()).get(PantriesViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        mSearchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)

        val swipeContainer = view.findViewById(R.id.listWrapper) as? SwipeRefreshLayout
        swipeContainer?.setOnRefreshListener {
            model.reloadPantries()
            swipeContainer.isRefreshing = false
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)

        with(recyclerView) {
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
            layoutManager = LinearLayoutManager(context)
        }

        model.pantries.observe(this, Observer<List<Pantry>> { pantries ->
            recyclerView.adapter = MyItemRecyclerViewAdapter(pantries ?: emptyList(), listener)
        })

        model.networkState.observe(this, Observer { result ->
            val errorLoading = view.findViewById(R.id.error) as? TextView
            val pb = view.findViewById(R.id.pbLoading) as? ProgressBar
            when (result) {
                NetworkState.SUCCESS -> {
                    errorLoading?.visibility = TextView.GONE
                    pb?.visibility = ProgressBar.GONE
                }
                NetworkState.LOADING -> pb?.visibility = ProgressBar.VISIBLE
                NetworkState.FAILURE -> {
                    errorLoading?.visibility = TextView.VISIBLE
                    pb?.visibility = ProgressBar.GONE
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu, menu)
        requireActivity().title = getString(R.string.app_name)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        mSearchQuery?.let {
            searchItem.expandActionView()
            searchView.setQuery(it, false)
            searchView.clearFocus()
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                mSearchQuery = query
                model.filter(query)

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (isVisible) {
                    mSearchQuery = newText
                    model.filter(newText)
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!mSearchQuery.isNullOrEmpty()) {
            outState.putString(KEY_SEARCH_QUERY, mSearchQuery)
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
