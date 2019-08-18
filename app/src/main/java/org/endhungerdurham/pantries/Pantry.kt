package org.endhungerdurham.pantries

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class PantryList(val pantries: List<Pantry>)

@Serializable
data class Pantry(val organizations: String, val address: String, val city: String,
                  val days: String?, val hours: String?,
                  val phone: String?, val info: String?, val prereq: String?,
                  val latitude: Double, val longitude: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(organizations)
        writeString(address)
        writeString(city)
        writeString(days)
        writeString(hours)
        writeString(phone)
        writeString(info)
        writeString(prereq)
        writeDouble(latitude)
        writeDouble(longitude)
    }

    override fun describeContents() = 0

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = object : Parcelable.Creator<Pantry> {
            override fun createFromParcel(parcel: Parcel) = Pantry(parcel)
            override fun newArray(size: Int): Array<Pantry?> = arrayOfNulls(size)
        }
    }
}