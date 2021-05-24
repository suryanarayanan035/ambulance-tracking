package com.sample.ambulancetracking.retrofit

import com.sample.common.BloodGroup
import com.sample.common.Gender
import com.sample.common.HospitalType

data class LoginPayload(
    val loginDetails: LoginDetailsPayload
)

data class LoginDetailsPayload(
    val userId: String,
    val password: String,
)

data class RegisterPayload(
    val user: UserPayload,
)

data class UserPayload(
    val name: String,
    val mobile: String,
    val age: Int,
    val gender: Gender,
    val bloodGroup: BloodGroup,
    val address: AddressPayload,
    val location: LocationPayload,
    val password: String,
)

data class AddressPayload(
    val street: String,
    val district: String,
    val pincode: String,
)

data class LocationPayload(
    val type: String,
    val coordinates: List<Double>,
)

data class SearchPayload(
    val location: LocationPayload,
    val district: String,
    val hospitalType: HospitalType,
)

data class GetRequestDetailsPayload (
    val userId:String,
        )