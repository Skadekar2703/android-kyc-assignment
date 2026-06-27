package com.tommy.digitalbankkyc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class IfscResponseDto(
    @SerializedName("IFSC") val ifsc: String?,
    @SerializedName("BANK") val bank: String?,
    @SerializedName("BRANCH") val branch: String?,
    @SerializedName("CITY") val city: String?,
    @SerializedName("STATE") val state: String?,
    @SerializedName("MICR") val micr: String?
)
