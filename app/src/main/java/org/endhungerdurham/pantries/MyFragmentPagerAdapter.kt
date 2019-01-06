package org.endhungerdurham.pantries
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.lang.RuntimeException

val MAP_PAGE = 0
val LIST_PAGE = 1

class MyFragmentPagerAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    internal val PAGE_COUNT = 2
    private val tabTitles = mapOf(MAP_PAGE to context.getString(R.string.map), LIST_PAGE to context.getString(R.string.pantries))

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            MAP_PAGE -> return MapFragment()
            LIST_PAGE -> return PantriesFragment()
            else -> throw RuntimeException("Invalid tab position: ${position}")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}