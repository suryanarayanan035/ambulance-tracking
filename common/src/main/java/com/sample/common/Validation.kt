package com.sample.common

import androidx.core.text.isDigitsOnly

val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")

fun String.isPassword() = passwordRegex.find(this) != null

fun String.isMobile() = isDigitsOnly() && length == 10

fun String.isName() = length > 3