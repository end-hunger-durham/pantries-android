package org.endhungerdurham.pantries.ui.adapter
import android.content.Context
import androidx.fragment.app.FragmentManager
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.container.RootListFragment
import org.endhungerdurham.pantries.ui.container.RootMapFragment

const val MAP_PAGE = 0
const val LIST_PAGE = 1
const val PAGE_COUNT = 2

class MyFragmentPagerAdapter(fm: FragmentManager, context: Context) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    private val tabTitles = mapOf(MAP_PAGE to context.getString(R.string.map), LIST_PAGE to context.getString(R.string.pantries))

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            MAP_PAGE -> RootMapFragment()
            LIST_PAGE -> RootListFragment()
            else -> throw RuntimeException("Invalid tab position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}