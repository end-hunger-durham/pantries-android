package org.endhungerdurham.pantries

import android.support.v4.app.Fragment
import android.widget.TextView
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View


class MapFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        return view
    }

    companion object {
        fun newInstance(): MapFragment {
            val args = Bundle()
            val fragment = MapFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}
