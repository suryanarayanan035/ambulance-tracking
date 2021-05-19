package com.sample.common

import androidx.core.text.isDigitsOnly

val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")

fun String.isPassword() = passwordRegex.find(this) != null

fun String.isMobile() = isDigitsOnly() && length == 10

fun String.isName() = length > 3

fun String.isAge() = isDigitsOnly() && length in (1..3)

fun String.isGender() = this in Gender.values().map { it.gender }

fun String.isBloodGroup() = this in BloodGroup.values().map { it.blood }

fun String.isStrong() = length > 5

fun String.isPincode() = isDigitsOnly() && length == 6

fun validateUserPayload(
    name: String,
    mobile: String,
    age: String,
    gender: String,
    bloodGroup: String,
    street: String,
    district: String,
    pincode: String,
    password: String,
) = listOf(
    name.isName(),
    mobile.isMobile(),
    age.isAge(),
    gender.isGender(),
    bloodGroup.isBloodGroup(),
    street.isStrong(),
    district.isStrong(),
    pincode.isPincode(),
    password.isPassword()
).all { it }
