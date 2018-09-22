package org.endhungerdurham.pantries

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.endhungerdurham.pantries.ItemFragment.OnListFragmentInteractionListener;
import org.endhungerdurham.pantries.dummy.DummyContent
import org.w3c.dom.Text

/*import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions*/

class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val mapFragment = fragmentManager
                .findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)*/
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        val lview: FrameLayout = findViewById(R.id.list_container)
        val myText: TextView = TextView(this)
        myText.setText(item?.details)
        lview.addView(myText)

    }

    /*override fun onMapReady(map: GoogleMap) {
        map.addMarker(MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker"))
    }*/
}
