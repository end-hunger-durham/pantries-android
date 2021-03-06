package org.endhungerdurham.pantries.ui.fragment

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.backend.Pantry
import org.endhungerdurham.pantries.ui.utils.setColorFilter
import org.endhungerdurham.pantries.ui.utils.startGoogleMapsIntent
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val ARG_PANTRY = "pantry"
private const val DEFAULT_ZOOM = 16.0f

class DetailsFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback {

    private lateinit var model: PantriesViewModel
    private var mMapView: MapView ?= null
    private lateinit var mPantry: Pantry

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

        mPantry = arguments?.getParcelable(ARG_PANTRY) ?: throw RuntimeException("Error: Pantry data missing from DetailsFragment")

        mMapView = view.findViewById(R.id.fragment_details_map_view)
        mMapView?.onCreate(savedInstanceState)
        mMapView?.visibility = View.INVISIBLE
        mMapView?.getMapAsync(this)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        requireActivity().title = mPantry.organizations
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.uiSettings?.isMapToolbarEnabled = false

        map?.setOnMapLoadedCallback {
            view?.findViewById<MapView>(R.id.fragment_details_map_view)?.visibility = View.VISIBLE
            view?.findViewById<ProgressBar>(R.id.fragment_details_map_loading)?.visibility = View.GONE
        }

        map?.setOnMarkerClickListener {
            startGoogleMapsIntent(mPantry, requireContext())
            true
        }

        map?.setOnMapClickListener {
            startGoogleMapsIntent(mPantry, requireContext())
        }

        // Map setup
        val pos = LatLng(mPantry.latitude, mPantry.longitude)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM))
        map?.addMarker(MarkerOptions().position(pos).title(mPantry.organizations))
    }

    private fun fillDetails(view: View?, pantry: Pantry) {
        fun buildDetailsText(list: List<String?>, separator: String = "\n"): String {
            return mutableListOf<String>().apply {
                for (str in list) {
                    if (!str.isNullOrBlank()) {
                        add(str)
                    }
                }
            }.joinToString(separator)
        }

        view?.findViewById<TextView>(R.id.phone_number_field)?.let { textView ->
            textView.text = mPantry.phone
            textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            textView.setOnClickListener{
                startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mPantry.phone, null)))
            }
        }

        view?.findViewById<Button>(R.id.directions)?.let { button ->
            // change directions icon to white
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_directions_24px)
            setColorFilter(icon, ContextCompat.getColor(requireContext(), R.color.details_directions_icon))
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

            button.setOnClickListener {
                startGoogleMapsIntent(mPantry, requireContext())
            }
        }

        val addressText = view?.findViewById<TextView>(R.id.address_field)
        addressText?.append(buildDetailsText(listOf(pantry.address, pantry.city)))

        val availabilityText = view?.findViewById<TextView>(R.id.availability_field)
        availabilityText?.append(buildDetailsText(listOf(pantry.days, pantry.hours)))

        val qualificationsText = view?.findViewById<TextView>(R.id.qualifications_field)
        qualificationsText?.append(pantry.prereq)

        val infoText = view?.findViewById<TextView>(R.id.info_field)
        infoText?.append(pantry.info)
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
        mMapView = null
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
        fun newInstance(item: Pantry) : DetailsFragment {
            val args = Bundle()
            args.putParcelable(ARG_PANTRY, item)
            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
