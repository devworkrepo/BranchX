package com.branchx.agent.helper.drawer

import android.os.Parcel
import android.os.Parcelable


class SubTitle : Parcelable {
    var name: String? = null

    constructor(name: String) {
        this.name = name
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.name)
    }

     constructor(`in`: Parcel) {
        this.name = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<SubTitle> {
        override fun createFromParcel(parcel: Parcel): SubTitle {
            return SubTitle(parcel)
        }

        override fun newArray(size: Int): Array<SubTitle?> {
            return arrayOfNulls(size)
        }
    }


}
