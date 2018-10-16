package org.endhungerdurham.pantries

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.endhungerdurham.pantries.dummy.DummyContent

// TODO: Update parameters with actual pantry data
private const val ARG_ITEM_NUMBER = "item_number"

class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val detailsText = view.findViewById<TextView>(R.id.details_text)
        detailsText.setText(arguments?.getString(ARG_ITEM_NUMBER))
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(item: DummyContent.DummyItem?) : DetailsFragment {
            val args = Bundle()
            args.putString(ARG_ITEM_NUMBER, item.toString())
            val fragment = DetailsFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}
