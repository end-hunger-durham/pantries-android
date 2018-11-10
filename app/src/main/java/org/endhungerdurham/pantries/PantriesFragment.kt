package org.endhungerdurham.pantries

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PantriesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frament_pantries, container, false)

        // Initialize pantries fragment with pantries list if fresh instance
        if (savedInstanceState == null) {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.pantries_frame, ListFragment())
            transaction?.commit()
        }

        return view
    }
}