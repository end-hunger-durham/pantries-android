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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val ARG_PANTRY = "pantry"
private const val DEFAULT_ZOOM = 16.0f

class DetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var model: PantriesViewModel
    private var mMapView: MapView ?= null
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

        mPantry = arguments?.getParcelable(ARG_PANTRY)

        mMapView = view.findViewById(R.id.fragment_details_map_view)
        mMapView?.onCreate(savedInstanceState)
        mMapView?.visibility = View.INVISIBLE
        mMapView?.getMapAsync(this)

        val phoneButton = view?.findViewById<Button>(R.id.phone)
        val phoneData = mPantry?.phone
        if (phoneData.isNullOrBlank()) {
            phoneButton?.visibility = View.GONE
        } else {
            phoneButton?.text = phoneData
            phoneButton?.setOnClickListener{
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneData, null)))
            }
        }

        fillDetails(view, mPantry)

        return view
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()

        val loading = view?.findViewById<ProgressBar>(R.id.fragment_details_map_loading)
        loading?.visibility = View.VISIBLE

        requireActivity().findViewById<TabLayout>(R.id.sliding_tabs).visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        requireActivity().title = mPantry?.organizations ?: "Pantry"
    }

    override fun onMapReady(map: GoogleMap?) {
        mPantry?.let { pantry ->
            val pos = LatLng(pantry.latitude, pantry.longitude)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM))
            map?.addMarker(MarkerOptions().position(pos).title(pantry.organizations))
        }

        map?.setOnMapLoadedCallback {
            view?.findViewById<MapView>(R.id.fragment_details_map_view)?.visibility = View.VISIBLE
            view?.findViewById<ProgressBar>(R.id.fragment_details_map_loading)?.visibility = View.GONE
        }
    }

    private fun fillDetails(view: View?, pantry: Pantry?) {
        val addressText = view?.findViewById<TextView>(R.id.address_field)
        addressText?.append("${pantry?.address} ${pantry?.city}")

        val availabilityText = view?.findViewById<TextView>(R.id.availability_field)
        availabilityText?.append("${pantry?.days} ${pantry?.hours}")

        val qualificationsText = view?.findViewById<TextView>(R.id.qualifications_field)
        qualificationsText?.append("${pantry?.prereq}")

        val infoText = view?.findViewById<TextView>(R.id.info_field)
        infoText?.append("${pantry?.info}")
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
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
