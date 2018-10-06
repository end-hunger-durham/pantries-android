package org.endhungerdurham.pantries

import android.support.v4.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.endhungerdurham.pantries.dummy.DummyContent

class PantriesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        /* Inflate the layout for this fragment */
        val view = inflater.inflate(R.layout.frament_pantries, container, false)

        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.pantries_frame, ItemsFragment())
        transaction.commit()

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = PantriesFragment()
    }
}