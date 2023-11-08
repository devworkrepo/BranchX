package com.branchx.agent.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryListResponse(
    val status: Int,
    val message: String,
    val count: Int,
    @SerializedName("data")
    val countryList: List<Country>
) : Parcelable

@Parcelize
data class StateListResponse(
    val status: Int,
    val message: String,
    val count: Int,
    @SerializedName("data")
    val stateList: List<State>
) : Parcelable

@Parcelize

data class CityListResponse(
    val status: Int,
    val message: String,
    val count: Int,
    @SerializedName("data")
    val cityList: List<City>
) : Parcelable

@Parcelize
data class Country(
    @SerializedName("CountryId")
    val countryId: Int,
    @SerializedName("Country")
    val countryName: String
) : Parcelable

@Parcelize
data class State(
    @SerializedName("StateName")
    val stateName: String
) : Parcelable

@Parcelize
data class City(
    @SerializedName("CityId")
    val cityId: Int,
    @SerializedName("city")
    val cityName: String
) : Parcelable

//// User List Response

@Parcelize
data class UserListResponse(
    @SerializedName("Namame")
    val name: String,
    @SerializedName("UserId")
    val userId: Int,
    @SerializedName("Addess")
    val address: String,
    @SerializedName("Citity")
    val cityName: String,
    @SerializedName("akskhehshs")
    val pancardNo: String,
    @SerializedName("AaddNo")
    val aadharNo: String,
    @SerializedName("GsttNo")
    val gstNo: String,
    @SerializedName("MobeNo")
    val mobileNo: String,
    @SerializedName("9876543211")
    val altMobileNo: String,
    @SerializedName("Prof")
    val userProfile: String,
    @SerializedName("UserParent")
    val parent: String,
    @SerializedName("UserType")
    val userType: String,
    @SerializedName("Parent")
    val parentName: String,
    @SerializedName("Balance")
    val balance: String
) : Parcelable

