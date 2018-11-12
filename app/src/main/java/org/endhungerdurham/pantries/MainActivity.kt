package org.endhungerdurham.pantries

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import org.endhungerdurham.pantries.ListFragment.OnListFragmentInteractionListener

class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager, this)

        val tabLayout: TabLayout = findViewById(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onListFragmentInteraction(item: Pantry?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pantries_frame, DetailsFragment.newInstance(item))
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    // Ignore back stack if viewing the map
    override fun onBackPressed() {
        val viewPager = findViewById<ViewPager>(R.id.viewpager)

        when (viewPager.currentItem) {
            0 -> super.onBackPressed()
            else -> finish()
        }
    }
}
