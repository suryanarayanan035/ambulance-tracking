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
    val requestStatus:String,

        )
data class GetLocationUpdatesResponse (
    val hasError:Boolean,
    val locationUpdate:LocationUpdates,
    val ambulanceDetails:AmbulanceResponse,

        )

data class AmbulanceResponse(
    val driverName:String,
    val driverMobile:String,
    val vehicleNo: String,
    val hospital:String,
)

data class CreateRequestAndJourneyResponse (
    val hasError:Boolean,
    val requestId:String
    )
data class GetRequestsByUser (
    val name:String,
    val requestStatus:String,
    val journeyStatus:String,
    val _id:String,
)
data class GetRequestsByUserResposne (
    val hasError:Boolean,
    val requests:List<GetRequestsByUser>
        )
data class Ambulance (
    val driverName:String,
    val driverMobile:String,
    val hospital:String,
    val _id:String,
    val hospitalName:String,
        )
data class GetNearbyAmbulancesResponse(
    val areAmbulancesAvailable:Boolean,
    val ambulances:List<Ambulance>
)