package org.endhungerdurham.pantries.ui

import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import org.endhungerdurham.pantries.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.ListFragment.OnListFragmentInteractionListener
import org.endhungerdurham.pantries.ui.adapter.MyFragmentPagerAdapter
import org.endhungerdurham.pantries.ui.viewmodel.PantriesViewModel

private const val KEY_SEARCH_QUERY = "search_query"

class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {
    private var mSearchQuery: String ?= null
    private lateinit var model: PantriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSearchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
        model = ViewModelProviders.of(this).get(PantriesViewModel::class.java)

        supportFragmentManager.addOnBackStackChangedListener {
            val shouldEnableBack: Boolean = supportFragmentManager.backStackEntryCount > 0
            supportActionBar?.setDisplayHomeAsUpEnabled(shouldEnableBack)
        }

        // Used to handle screen rotation while DetailsFragment is running
        val shouldEnableBack: Boolean = supportFragmentManager.backStackEntryCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(shouldEnableBack)

        val viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager, this)

        val tabLayout: TabLayout = findViewById(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }

    override fun onListFragmentInteraction(item: Pantry) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.root_list_fragment, DetailsFragment.newInstance(item))
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_refresh -> {
            model.reloadPantries()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()

        menuInflater.inflate(R.menu.main_menu, menu)
        title = getString(R.string.app_name)

        val refreshItem = menu?.findItem(R.id.action_refresh)
        val refreshIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_cached_24px)
        refreshIcon?.setColorFilter(ContextCompat.getColor(this, R.color.menu), PorterDuff.Mode.SRC_ATOP)
        refreshItem?.icon = refreshIcon

        val searchItem = menu?.findItem(R.id.action_search)
        val searchIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24px)
        searchIcon?.setColorFilter(ContextCompat.getColor(this, R.color.menu), PorterDuff.Mode.SRC_ATOP)
        searchItem?.icon = searchIcon

        val searchView = searchItem?.actionView as SearchView
        if (!mSearchQuery.isNullOrBlank()) {
            searchItem.expandActionView()
            searchView.setQuery(mSearchQuery, false)
            searchView.clearFocus()
            refreshItem?.isVisible = false
        }

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                refreshItem?.isVisible = true
                invalidateOptionsMenu()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                refreshItem?.isVisible = false
                invalidateOptionsMenu()
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(query: String): Boolean {
                mSearchQuery = query
                model.filter(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                mSearchQuery = query
                model.filter(query)

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (!mSearchQuery.isNullOrEmpty()) {
            outState?.putString(KEY_SEARCH_QUERY, mSearchQuery)
        }
        super.onSaveInstanceState(outState)
    }
}
