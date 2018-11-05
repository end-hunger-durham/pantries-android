package org.endhungerdurham.pantries

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.net.URL
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer

private const val ARG_PANTRY_LIST = "pantry_list"

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ItemsFragment.OnListFragmentInteractionListener] interface.
 */
class ItemsFragment : Fragment(), CoroutineScope {

    private var listener: OnListFragmentInteractionListener? = null
    private var pantries: List<Pantry> ?= null
    private var job: Job ?= null

    override val coroutineContext = Job() + Dispatchers.Main

    private suspend fun fetchPantries(): List<Pantry> {
        return withContext(Dispatchers.IO) {
            val json = async(Dispatchers.IO) {
                URL(requireContext().getString(R.string.pantries_json_url)).readText()
            }.await()
            JSON.parse(PantryList.serializer(), json).pantries
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            pantries = savedInstanceState.getParcelableArrayList<Pantry>(ARG_PANTRY_LIST)?.toList()
        } else {
            job = launch {
                pantries = fetchPantries()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_items_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                launch {
                    job?.join()
                    adapter = MyItemRecyclerViewAdapter(pantries ?: emptyList(), listener)
                }
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(ARG_PANTRY_LIST, ArrayList(pantries))
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
        fun onListFragmentInteraction(item: Pantry?)
    }
}
