package com.example.labs_app.model

import android.os.Parcel
import android.os.Parcelable

/** Данные для предзаполнения экрана входа (возврат из SignUp). */
data class SignInPrefill(
    val username: String,
    val email: String,
    val password: String
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(password)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SignInPrefill> {
        override fun createFromParcel(parcel: Parcel): SignInPrefill = SignInPrefill(
            username = parcel.readString().orEmpty(),
            email = parcel.readString().orEmpty(),
            password = parcel.readString().orEmpty()
        )
        override fun newArray(size: Int): Array<SignInPrefill?> = arrayOfNulls(size)
    }
}
