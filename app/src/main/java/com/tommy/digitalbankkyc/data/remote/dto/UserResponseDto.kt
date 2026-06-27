package com.tommy.digitalbankkyc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("users") val users: List<UserDto>
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("birthDate") val birthDate: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("image") val image: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("bank") val bank: BankDto
)

data class AddressDto(
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("country") val country: String
)

data class BankDto(
    @SerializedName("cardNumber") val cardNumber: String,
    @SerializedName("cardType") val cardType: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("iban") val iban: String
)
