package learning.self.kotlin.projectmanager.models

import android.os.Parcel
import android.os.Parcelable

data class Task (
    var title : String = "",
    val createdBy : String = "",
    var cards : ArrayList<Card> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeTypedList(cards)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
