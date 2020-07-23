package learning.self.kotlin.projectmanager.models

import android.os.Parcel
import android.os.Parcelable

data class SelectedMembers (
    var id : String = "",
    var image : String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(image)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<SelectedMembers> {
        override fun createFromParcel(parcel: Parcel): SelectedMembers {
            return SelectedMembers(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMembers?> {
            return arrayOfNulls(size)
        }
    }
}