package org.endhungerdurham.pantries.ui.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.container.RootListFragment
import org.endhungerdurham.pantries.ui.container.RootMapFragment
import org.endhungerdurham.pantries.ui.utils.VerticalImageSpan


const val MAP_PAGE = 0
const val LIST_PAGE = 1
const val PAGE_COUNT = 2

class MyFragmentPagerAdapter(fm: FragmentManager, val context: Context) : androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val tabTitles = mapOf(MAP_PAGE to context.getString(R.string.map), LIST_PAGE to context.getString(R.string.list))
    private val tabIcons = mapOf(MAP_PAGE to R.drawable.ic_navigation_24px, LIST_PAGE to R.drawable.ic_list_24px)

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
        val icon = when (position) {
            MAP_PAGE -> ContextCompat.getDrawable(context, R.drawable.ic_navigation_24px)
            LIST_PAGE -> ContextCompat.getDrawable(context, R.drawable.ic_list_24px)
            else -> throw java.lang.RuntimeException("Invalid tab position: $position")
        }
        return icon?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)

            val spannable = SpannableString("   " + tabTitles[position])
            val imageSpan = VerticalImageSpan(it)
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable
        }
    }
}