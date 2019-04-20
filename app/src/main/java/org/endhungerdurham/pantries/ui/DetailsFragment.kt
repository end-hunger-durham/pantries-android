package org.endhungerdurham.pantries.ui

import android.arch.lifecycle.ViewModelProviders
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
import android.support.design.widget.TabLayout
import android.view.*
import android.widget.ProgressBar
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val ARG_PANTRY = "pantry"
private const val DEFAULT_ZOOM = 16.0f

class DetailsFragment : Fragment() {

    private lateinit var model: PantriesViewModel
    private var mPantry: Pantry?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(requireActivity()).get(PantriesViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // clear filter after choosing pantry
        model.filter("")

        val view = inflater.inflate(R.layout.fragment_details, container, false)

        val pantry: Pantry? = arguments?.getParcelable(ARG_PANTRY)
        mPantry = pantry

        val loading = view.findViewById(R.id.fragment_details_map_loading) as? ProgressBar
        loading?.visibility = View.VISIBLE

        val map = view.findViewById(R.id.fragment_details_map_view) as? MapView
        map?.onCreate(savedInstanceState)
        map?.visibility = View.INVISIBLE
        map?.getMapAsync {
            pantry?.let { pantry ->
                val pos = LatLng(pantry.latitude, pantry.longitude)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM))
                it.addMarker(MarkerOptions().position(pos).title(pantry.organizations))
            }

            it.setOnMapLoadedCallback {
                map.visibility = View.VISIBLE
                loading?.visibility = View.GONE
            }
        }

        val phone: Button = view.findViewById(R.id.phone)
        if (pantry?.phone == null || pantry.phone == "") {
            phone.visibility = View.GONE
        } else {
            phone.text = pantry.phone
            phone.setOnClickListener{
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pantry.phone, null)))
            }
        }

        fillDetails(view, pantry)

        return view
    }

    override fun onStart() {
        super.onStart()
        requireActivity().findViewById<TabLayout>(R.id.sliding_tabs).visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        requireActivity().title = mPantry?.organizations ?: "Pantry"
    }

    private fun fillDetails(view: View, pantry: Pantry?) {
        val addressText = view.findViewById<TextView>(R.id.address_field)
        addressText.append("${pantry?.address} ${pantry?.city}")

        val availabilityText = view.findViewById<TextView>(R.id.availability_field)
        availabilityText.append("${pantry?.days} ${pantry?.hours}")

        val qualificationsText = view.findViewById<TextView>(R.id.qualifications_field)
        qualificationsText.append("${pantry?.prereq}")

        val infoText = view.findViewById<TextView>(R.id.info_field)
        infoText.append("${pantry?.info}")
    }

    companion object {
        @JvmStatic
        fun newInstance(item: Pantry?) : DetailsFragment {
            val args = Bundle()
            args.putParcelable(ARG_PANTRY, item)
            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
