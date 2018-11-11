package org.endhungerdurham.pantries

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val ARG_PANTRY = "pantry"

// TODO: prettier display of information
// TODO: ACTION_DIAL intent for phone number
class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        val pantry: Pantry? = arguments?.getParcelable(ARG_PANTRY)

        val map = view.findViewById(R.id.liteMapView) as? MapView
        map?.onCreate(savedInstanceState)
        map?.getMapAsync {
            pantry?.let { pantry ->
                val pos = LatLng(pantry.latitude, pantry.longitude)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f))
                it.addMarker(MarkerOptions().position(pos).title(pantry.organizations))
            }
        }

        fillDetails(view, pantry)

        return view
    }

    private fun fillDetails(view: View, pantry: Pantry?) {
        val addressText = view.findViewById<TextView>(R.id.address_field)
        addressText.append("${pantry?.address} ${pantry?.city}")

        val avaialabilityText = view.findViewById<TextView>(R.id.availability_field)
        avaialabilityText.append("${pantry?.days} ${pantry?.hours}")

        val qualsText = view.findViewById<TextView>(R.id.qualifications_field)
        qualsText.append("${pantry?.prereq}")

        val infoText = view.findViewById<TextView>(R.id.info_field)
        infoText.append("${pantry?.info}")
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
