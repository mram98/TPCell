package com.nva.tpcell.models

import android.os.Parcel
import android.os.Parcelable

data class Drive(
    // Parcelable Drive data class
    val name: String = "",
    val desc: String = "",
    val aggregate_10th: Int = 0,
    val aggregate_12th: Int = 0,
    val aggregate_college: Int = 0

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeInt(aggregate_10th)
        parcel.writeInt(aggregate_12th)
        parcel.writeInt(aggregate_college)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Drive> {
        override fun createFromParcel(parcel: Parcel): Drive {
            return Drive(parcel)
        }

        override fun newArray(size: Int): Array<Drive?> {
            return arrayOfNulls(size)
        }
    }
}