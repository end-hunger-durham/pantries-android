package org.endhungerdurham.pantries.ui

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.view.Menu
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.ListFragment.OnListFragmentInteractionListener
import org.endhungerdurham.pantries.ui.adapter.MyFragmentPagerAdapter
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private lateinit var model: PantriesViewModel
    private var mMenu: Menu ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.addOnBackStackChangedListener {
            val shouldEnableBack: Boolean = supportFragmentManager.backStackEntryCount > 0
            supportActionBar?.setDisplayHomeAsUpEnabled(shouldEnableBack)
        }

        val shouldEnableBack: Boolean = supportFragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(shouldEnableBack)

        val viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager, this)

        val tabLayout: TabLayout = findViewById(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(viewPager)

        model = ViewModelProviders.of(this).get(PantriesViewModel::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        model.filter("")
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onListFragmentInteraction(item: Pantry) {
        // HACK: Setting iconified to indicate text should not be filtered
        val searchItem = mMenu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.isIconified = true

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pantries_frame, DetailsFragment.newInstance(item))
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        model.filter("")
        super.onBackPressed()
    }
}
