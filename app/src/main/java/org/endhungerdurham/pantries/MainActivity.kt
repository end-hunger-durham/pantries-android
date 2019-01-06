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
    }

    override fun onListFragmentInteraction(item: Pantry) {
        item.address?.let {
            model.filterPantries(it)
        }

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
            0 -> {
                model.filterPantries("")
                super.onBackPressed()
            }
            else -> finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        mSearchQuery?.let {
            searchView.setQuery(it, false)
        }

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

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(KEY_SEARCH_QUERY, mSearchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        mSearchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY)
    }
}
