package com.example.labs_app.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Модель пользователя для передачи между Fragment (Parcelable).
 */
data class User(
    val username: String,
    val email: String,
    val password: String,
    val nickname: String = ""
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(nickname)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User = User(
            username = parcel.readString().orEmpty(),
            email = parcel.readString().orEmpty(),
            password = parcel.readString().orEmpty(),
            nickname = parcel.readString().orEmpty()
        )
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}
