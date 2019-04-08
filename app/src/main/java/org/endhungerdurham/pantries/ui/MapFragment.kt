package org.endhungerdurham.pantries.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import android.content.pm.PackageManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ProgressBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.info_window_item.view.*
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private val DEFAULT_ZOOM = 11.5f
private val DURHAM_NC: LatLng = LatLng(35.9940, -78.8986)
private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

private val KEY_CAMERA_POSITION = "camera_position"
private val KEY_LOCATION = "location"

// TODO: Change icon color depending on whether it is open/closed
class MapFragment : Fragment() {
    private var mLastLocation: LatLng ?= null
    private var mLocationPermissionGranted: Boolean = false
    private var mMap: GoogleMap ?= null
    private var mMapView: MapView ?= null
    private lateinit var model: PantriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = ViewModelProviders.of(requireActivity()).get(PantriesViewModel::class.java)

        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(savedInstanceState.getParcelable(KEY_CAMERA_POSITION)))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val loading = rootView.findViewById(R.id.fragment_map_loading) as? ProgressBar
        loading?.visibility = View.VISIBLE

        mMapView = rootView.findViewById(R.id.fragment_map_view)
        mMapView?.onCreate(savedInstanceState)
        mMapView?.onResume()

        mMapView?.getMapAsync{
            mMap = it
            updateMyLocationUI()
            updateCamera(DURHAM_NC)

            it.setOnMapLoadedCallback {
                mMapView?.visibility = View.VISIBLE
                loading?.visibility = View.GONE
            }

            it.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter{
                override fun getInfoContents(p0: Marker?): View? {
                    val infoView = inflater.inflate(R.layout.info_window_item, container, false)
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

            it.setOnInfoWindowClickListener { marker ->
                val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpager)
                viewPager.post{
                    viewPager.arrowScroll(View.FOCUS_RIGHT)
                }

                val fragmentTransaction = childFragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.fragment_wrapper, DetailsFragment.newInstance(marker.tag as? Pantry))
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

            model.pantries.observe(this, Observer<List<Pantry>> { pantries ->
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

        return rootView
    }

    private fun getLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
            return true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            return false
        }
    }

    private fun updateCamera(pos: LatLng) {
        mLastLocation = pos
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM))
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mLocationPermissionGranted = true
                    updateMyLocationUI()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mMap?.let {
            outState.putParcelable(KEY_CAMERA_POSITION, it.cameraPosition)
            outState.putParcelable(KEY_LOCATION, mLastLocation)
        }
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }
}
