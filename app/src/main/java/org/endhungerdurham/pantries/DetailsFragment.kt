package org.endhungerdurham.pantries

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

private const val ARG_PANTRY = "pantry"

// TODO: prettier display of information
// TODO: ACTION_DIAL intent for phone number
class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        fillDetails(view, arguments?.getParcelable(ARG_PANTRY))

        return view
    }

    private fun fillDetails(view: View, pantry: Pantry?) {
        val addressText = view.findViewById<TextView>(R.id.address)
        addressText.append("\n" + pantry?.address + pantry?.city)

        val avaialabilityText = view.findViewById<TextView>(R.id.avaialability)
        avaialabilityText.append("\n" + pantry?.days + pantry?.hours)

        val qualsText = view.findViewById<TextView>(R.id.qualifications)
        qualsText.append("\n" + pantry?.prereq)

        val infoText = view.findViewById<TextView>(R.id.info)
        infoText.append("\n" + pantry?.info)
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
