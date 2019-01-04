package org.endhungerdurham.pantries

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.net.Uri
import android.view.*

private const val ARG_PANTRY = "pantry"
private val DEFAULT_ZOOM = 16.0f

class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        val pantry: Pantry? = arguments?.getParcelable(ARG_PANTRY)

        val phone: Button = view.findViewById(R.id.phone)
        pantry?.phone?.let { number ->
            phone.text = number
            phone.setOnClickListener{
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
            }
        }

        val map = view.findViewById(R.id.liteMapView) as? MapView
        map?.onCreate(savedInstanceState)
        map?.getMapAsync {
            pantry?.let { pantry ->
                val pos = LatLng(pantry.latitude, pantry.longitude)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM))
                it.addMarker(MarkerOptions().position(pos).title(pantry.organizations))
            }
        }

        fillDetails(view, pantry)

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
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

    override fun onResume() {
        super.onResume()
        requireActivity().invalidateOptionsMenu()
    }

    override fun onStop() {
        super.onStop()
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
