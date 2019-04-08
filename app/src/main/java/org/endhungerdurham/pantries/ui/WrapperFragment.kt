package org.endhungerdurham.pantries.ui

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R

private const val ARG_FRAG_TYPE = "frag_type"

class WrapperFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wrapper, container, false)

        val fragType = arguments?.getString(ARG_FRAG_TYPE)

        // Initialize wrapper fragment with root fragments (Map or List)
        if (savedInstanceState == null) {
            when (fragType) {
                "map" -> MapFragment()
                "list" -> ListFragment()
                else -> null
            }?.let { fragment ->
                val transaction = childFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_wrapper, fragment)
                transaction.commit()
            }
        }

        return view
    }

    fun listItemSelect(item: Pantry) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_wrapper, DetailsFragment.newInstance(item))
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(fragType: String) : WrapperFragment {
            val args = Bundle()
            args.putString(ARG_FRAG_TYPE, fragType)
            val fragment = WrapperFragment()
            fragment.arguments = args
            return fragment
        }
    }
}