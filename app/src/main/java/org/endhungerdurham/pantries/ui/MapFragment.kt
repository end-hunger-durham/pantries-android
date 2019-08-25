package org.endhungerdurham.pantries.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.info_window_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.viewmodel.NetworkState
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val REFRESH_ANIMATION_DELAY: Long = 1000
private const val DEFAULT_ZOOM = 11.5f
private val DURHAM_NC: LatLng = LatLng(35.9940, -78.8986)

private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

private const val KEY_LOCATION = "location"

// TODO: Change icon color depending on whether it is open/closed
class MapFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback {
    private var mLastLocation: CameraPosition ?= null
    private var mLocationPermissionGranted: Boolean = false
    private var mMap: GoogleMap ?= null
    private var mMapView: MapView ?= null
    private lateinit var model: PantriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(requireActivity()).get(PantriesViewModel::class.java)
        mLastLocation = savedInstanceState?.getParcelable(KEY_LOCATION)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mMapView = rootView.findViewById(R.id.fragment_map_view)
        mMapView?.onCreate(savedInstanceState)
        mMapView?.getMapAsync(this)

        val swipeContainer = view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.map_refresh)
        swipeContainer?.setOnRefreshListener(null)
        swipeContainer?.isEnabled = false

        model.networkState.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                NetworkState.SUCCESS -> setRefreshing(false)
                NetworkState.LOADING -> setRefreshing(true)
                NetworkState.FAILURE -> {
                    Toast.makeText(view?.context, requireContext().getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                    setRefreshing(false)
                }
                else -> {}
            }
        })

        return rootView
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()

        requireActivity().findViewById<TabLayout>(R.id.sliding_tabs).visibility = View.VISIBLE
    }

    private fun setRefreshing(isRefreshing: Boolean) {
        val swipeContainer = view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.map_refresh)
        when (isRefreshing) {
            false -> {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(REFRESH_ANIMATION_DELAY)
                    swipeContainer?.isRefreshing = false
                    swipeContainer?.isEnabled = false
                }
            }
            true -> {
                swipeContainer?.isEnabled = true
                swipeContainer?.isRefreshing = true
            }
        }
    }

    private fun getLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
            true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun updateMyLocationUI() {
        try {
            if (mLocationPermissionGranted || getLocationPermission()) {
                mMap?.isMyLocationEnabled = true
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mLocationPermissionGranted = true
                    updateMyLocationUI()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        updateMyLocationUI()

        val cameraPosition = mLastLocation ?: CameraPosition.Builder().target(DURHAM_NC).zoom(DEFAULT_ZOOM).build()
        mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter{
            override fun getInfoContents(p0: Marker?): View? {
                val infoView = layoutInflater.inflate(R.layout.info_window_item, view?.parent as ViewGroup, false)
                val pantry: Pantry? = p0?.tag as? Pantry?

                infoView.info_org.text = pantry?.organizations

                val days = "Days: ${pantry?.days}"
                infoView.info_days.text = days

                val hours = "Hours: ${pantry?.hours}"
                infoView.info_hours.text = hours

                return infoView
            }

            override fun getInfoWindow(p0: Marker?): View? {
                return null
            }
        })

        map?.setOnInfoWindowClickListener { marker ->
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.root_map_fragment, DetailsFragment.newInstance(marker.tag as Pantry))
            fragmentTransaction?.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }

        model.pantries.observe(viewLifecycleOwner, Observer<List<Pantry>> { pantries ->
            mMap?.clear()

            for (pantry in pantries ?: emptyList()) {
                mMap?.addMarker(MarkerOptions()
                        .position(LatLng(pantry.latitude, pantry.longitude))
                        .title(pantry.organizations)
                        .alpha(0.75f))
                        ?.tag = pantry
            }
        })
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
        mMap?.clear()
        mMapView?.onDestroy()
        mMap = null
        mMapView = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mMap?.cameraPosition?.let {
            outState.putParcelable(KEY_LOCATION, it)
        }
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }
}
