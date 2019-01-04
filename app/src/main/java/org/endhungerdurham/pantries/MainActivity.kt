package org.endhungerdurham.pantries

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.widget.SearchView
import android.view.Menu
import org.endhungerdurham.pantries.ListFragment.OnListFragmentInteractionListener

val KEY_SEARCH_QUERY = "search_query"

class MainActivity : AppCompatActivity(), OnListFragmentInteractionListener {

    private lateinit var model: PantriesViewModel
    private var mSearchQuery: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager, this)

        val tabLayout: TabLayout = findViewById(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(viewPager)

        model = ViewModelProviders.of(this).get(PantriesViewModel::class.java)

        mSearchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
    }

    override fun onListFragmentInteraction(item: Pantry?) {
        /*menuInflater.inflate(R.menu.main_menu, mMenu)
        val searchItem = mMenu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        //searchView.isIconified = true
        //searchView.clearFocus()
        searchItem.collapseActionView()
        searchItem.isVisible = false*/

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                model.filterPantries(query)

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mSearchQuery = newText
                model.filterPantries(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        if (mSearchQuery != null) {
            searchView.post {
                searchView.isIconified = true
                searchView.onActionViewExpanded()
                searchView.setQuery(mSearchQuery, false)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(KEY_SEARCH_QUERY, mSearchQuery)
        super.onSaveInstanceState(outState)
    }
}
