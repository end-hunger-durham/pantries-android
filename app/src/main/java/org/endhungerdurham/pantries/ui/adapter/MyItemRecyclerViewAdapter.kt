package org.endhungerdurham.pantries.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import org.endhungerdurham.pantries.backend.Pantry
import org.endhungerdurham.pantries.R
import org.endhungerdurham.pantries.ui.fragment.ListFragment.OnListFragmentInteractionListener

class MyItemRecyclerViewAdapter(
        private val mPantries: List<Pantry>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Pantry
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mPantries[position]
        holder.mContentView.text = item.organizations
        val info = "${item.days}, ${item.hours}"
        holder.mInfoView.text = info

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mPantries.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.list_item_name
        val mInfoView: TextView = mView.list_item_info

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
