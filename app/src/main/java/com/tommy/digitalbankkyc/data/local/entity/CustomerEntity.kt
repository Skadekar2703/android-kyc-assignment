package com.tommy.digitalbankkyc.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String,
    val gender: String,
    val nationality: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    val email: String,
    val address: String,
    @ColumnInfo(name = "account_number") val accountNumber: String,
    val iban: String,
    val currency: String,
    val balance: Double,
    @ColumnInfo(name = "card_type") val cardType: String,
    @ColumnInfo(name = "ifsc_code") val ifscCode: String,
    val verified: Boolean,
    @ColumnInfo(name = "selfie_path") val selfiePath: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
