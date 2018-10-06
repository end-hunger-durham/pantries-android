package org.endhungerdurham.pantries
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.lang.RuntimeException

class MyFragmentPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm) {
    internal val PAGE_COUNT = 2
    private val tabTitles = arrayOf("List", "Map")

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return PantriesFragment.newInstance()
            1 -> return MapFragment.newInstance()
            else -> throw RuntimeException("Invalid tab position: " + position)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on item position
        return tabTitles[position]
    }
}