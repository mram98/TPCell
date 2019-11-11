package com.nva.tpcell.models

import android.os.Parcel
import android.os.Parcelable


data class Student(
    val email: String = "",
    val name: String = "",
    val enroll: String = "",
    val phone: String = "",
    val aggregate_10th: String = "",
    val aggregate_12th: String = "",
    val aggregate_college: String = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(enroll)
        parcel.writeString(phone)
        parcel.writeString(aggregate_10th)
        parcel.writeString(aggregate_12th)
        parcel.writeString(aggregate_college)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }
}