package org.endhungerdurham.pantries

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

private const val ARG_PANTRY = "item_number"

class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val detailsText = view.findViewById<TextView>(R.id.details_text)
        val pantry = arguments?.getParcelable<Pantry>(ARG_PANTRY)
        detailsText.setText(pantry?.organizations)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(item: Pantry?) : DetailsFragment {
            val args = Bundle()
            args.putParcelable(ARG_PANTRY, item)
            val fragment = DetailsFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}
