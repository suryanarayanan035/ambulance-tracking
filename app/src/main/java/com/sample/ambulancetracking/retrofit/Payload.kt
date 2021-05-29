package com.sample.ambulancetracking.retrofit

import com.sample.common.BloodGroup
import com.sample.common.Gender
import com.sample.common.HospitalType
import java.io.Serializable
import kotlin.properties.Delegates

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
    val bloodGroup: String,
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

class RequestJourneyDetails(name:String,age:Int,bloodGroup:String,gender:String,requestedBy:String,hospital:String,ambulance:String,isAccident:Boolean):Serializable {
    public var name = name
    public var age:Int = age
    public var bloodGroup:String = bloodGroup
    public var gender:String = gender
    public var requestedBy:String = requestedBy
    public var hospital:String = hospital
    public var ambulance: String = ambulance
    public var isAccident:Boolean = isAccident
    public var location:LocationPayload? = null

}

data class RequestJourneyDetailsPayload (
    var requestAndJourneyDetails: RequestJourneyDetails
        )

data class GetNearbyambulancePayload (
    var district:String,
    var type:String,
        )
