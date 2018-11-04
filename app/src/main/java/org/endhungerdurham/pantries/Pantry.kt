package org.endhungerdurham.pantries

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class PantryList(val pantries: List<Pantry>)

@Serializable
data class Pantry(val organizations: String, val address: String, val city: String,
                  val days: String, val hours: String,
                  val phone: String, val info: String, val prereq: String,
                  val latitude: Double, val longitude: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(organizations)
        parcel.writeString(address)
        parcel.writeString(city)
        parcel.writeString(days)
        parcel.writeString(hours)
        parcel.writeString(phone)
        parcel.writeString(info)
        parcel.writeString(prereq)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR = object : Parcelable.Creator<Pantry> {
            override fun createFromParcel(parcel: Parcel): Pantry {
                return Pantry(parcel)
            }

            override fun newArray(size: Int): Array<Pantry?> {
                return arrayOfNulls(size)
            }
        }
    }
}