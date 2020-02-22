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
    private val tabIcons = arrayOf(R.drawable.ic_navigation_24px, R.drawable.ic_list_24px)

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
        return ContextCompat.getDrawable(context, tabIcons[position])?.let { drawable ->
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

            // Create a title string that includes our tab icon at the beginning of the string
            val spannable = SpannableString("    " + tabTitles[position])
            val imageSpan = VerticalImageSpan(drawable)
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable
        }
    }
}