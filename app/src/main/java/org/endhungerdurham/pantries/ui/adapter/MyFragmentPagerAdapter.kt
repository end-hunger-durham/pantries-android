package org.endhungerdurham.pantries.ui.adapter
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.endhungerdurham.pantries.ui.WrapperFragment
import org.endhungerdurham.pantries.R
import java.lang.RuntimeException

const val MAP_PAGE = 0
const val LIST_PAGE = 1
const val PAGE_COUNT = 2

class MyFragmentPagerAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    private val tabTitles = mapOf(MAP_PAGE to context.getString(R.string.map), LIST_PAGE to context.getString(R.string.pantries))

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            MAP_PAGE -> WrapperFragment.newInstance("map")
            LIST_PAGE -> WrapperFragment.newInstance("list")
            else -> throw RuntimeException("Invalid tab position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}