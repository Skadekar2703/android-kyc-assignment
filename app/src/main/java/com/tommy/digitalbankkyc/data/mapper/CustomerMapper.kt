package com.tommy.digitalbankkyc.data.mapper

import com.tommy.digitalbankkyc.data.local.entity.CustomerEntity
import com.tommy.digitalbankkyc.data.remote.dto.IfscResponseDto
import com.tommy.digitalbankkyc.data.remote.dto.UserDto
import com.tommy.digitalbankkyc.domain.model.BankInfo
import com.tommy.digitalbankkyc.domain.model.Customer
import com.tommy.digitalbankkyc.utils.deterministicBalance
import com.tommy.digitalbankkyc.utils.deterministicIfsc

fun CustomerEntity.toDomain(): Customer {
    val fullName = "$firstName $lastName".trim()
    return Customer(
        id = id,
        firstName = firstName,
        lastName = lastName,
        fullName = fullName,
        avatarUrl = avatarUrl,
        dateOfBirth = dateOfBirth,
        gender = gender.replaceFirstChar { it.titlecase() },
        nationality = nationality,
        phoneNumber = phoneNumber,
        email = email,
        address = address,
        accountNumber = accountNumber,
        iban = iban,
        balance = balance,
        currency = currency,
        cardType = cardType,
        ifscCode = ifscCode,
        isVerified = verified,
        selfiePath = selfiePath,
        updatedAt = updatedAt
    )
}

fun UserDto.toEntity(existingCustomer: CustomerEntity?, syncedAt: Long): CustomerEntity {
    val fullAddress = listOf(
        address.address,
        address.city,
        address.state,
        address.postalCode,
        address.country
    ).filter { it.isNotBlank() }.joinToString(", ")

    return CustomerEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = image,
        dateOfBirth = birthDate,
        gender = gender,
        nationality = address.country,
        phoneNumber = phone,
        email = email,
        address = fullAddress,
        accountNumber = bank.cardNumber.filter { it.isDigit() }.ifBlank { "${id}0000000000" },
        iban = bank.iban,
        currency = bank.currency.ifBlank { "INR" },
        balance = deterministicBalance(id),
        cardType = bank.cardType,
        ifscCode = deterministicIfsc(id),
        verified = existingCustomer?.verified ?: false,
        selfiePath = existingCustomer?.selfiePath,
        updatedAt = syncedAt
    )
}

fun IfscResponseDto.toDomain(): BankInfo {
    return BankInfo(
        ifsc = ifsc.orEmpty(),
        bankName = bank.orEmpty(),
        branch = branch.orEmpty(),
        city = city.orEmpty(),
        state = state.orEmpty(),
        micr = micr.orEmpty()
    )
}
