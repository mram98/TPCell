package com.nva.tpcell.models

import android.os.Parcel
import android.os.Parcelable

data class Drive(
    //only email compulsory for now, may change later
    val name: String = "",
    val desc: String = "",
    val aggregate_10th: String = "",
    val aggregate_12th: String = "",
    val aggregate_college: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeString(aggregate_10th)
        parcel.writeString(aggregate_12th)
        parcel.writeString(aggregate_college)
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