package com.tommy.digitalbankkyc.domain.model

data class Customer(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val avatarUrl: String,
    val dateOfBirth: String,
    val gender: String,
    val nationality: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val accountNumber: String,
    val iban: String,
    val balance: Double,
    val currency: String,
    val cardType: String,
    val ifscCode: String,
    val isVerified: Boolean,
    val selfiePath: String?,
    val updatedAt: Long
)
