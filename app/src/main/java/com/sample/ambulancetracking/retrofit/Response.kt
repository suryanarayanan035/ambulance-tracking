package com.sample.ambulancetracking.retrofit

data class CheckResponse(
    val user: UserResponse,
    val isUserExists: Boolean
)

data class UserResponse(
    val name: String,
    val mobile: String,
    val gender: String,
    val bloodGroup: String,
    val address: AddressResponse,
    val location: LocationResponse,
)

data class AddressResponse(
    val street: String,
    val district: String,
    val pincode: String,
)

data class LocationResponse(
    val type: String,
    val coordinates: List<Double>,
)

data class SearchResponse(
    val hasError: Boolean,
    val ambulances: List<Any>
)

data class ActionResponse(
    val isValid: Boolean,
)

data class LocationUpdates (
    val currentLocation:List<LocationResponse>,
    val location:LocationResponse,
    val journeyStatus:String,

        )
data class GetLocationUpdatesResponse (
    val hasError:Boolean,
    val locationUpdate:LocationUpdates,
    val ambulance:AmbulanceResponse,

        )

data class AmbulanceResponse(
    val driverName:String,
    val driverMobile:String,
    val vehicleNo: String,
    val hospitalNo:String,
)
