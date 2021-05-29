package com.sample.hospitaladmin.home.models


data class HospitalCheckResponse (
    val isHospitalExists:Boolean ,
    val hospital: Hospital
    )

data class HospitalSignUpPayload (
    val hospital:Hospital
)

data class HospitalSignupResponse (
    val hasError:Boolean
        )
data class HospitalLoginDetails (
    public var hospitalId:String,
    public var password:String
    )
data class HospitalLoginPayload (
    public var loginDetails:HospitalLoginDetails
        )

/** Request and response classes for ambulance*/
data class Ambulance (
    public var driverName:String,
    public var driverMobile:String,
    public var password:String,
    public var vehicleNo:String,
    public var isAvailable:Boolean,
    public var hospital:String
        )

data class AmbulanceSignupPayload(
    public var ambulance:Ambulance
)
data class AmbulanceSignupResponse (
    val hasError:Boolean
        )

data class AmbulanceCheckResponse (
    var isAmbulanceExists:Boolean
        )
data class AmbulanceLoginDetails (
    var ambulanceId:String,
    var password:String,
    )
data class AmbulanceLoginPayload (
    var loginDetails:AmbulanceLoginDetails
)
data class LogInResponse(
        var isValid:Boolean
        )
data class Request(
    var _id:String,
    var age: Int,
    var isAccident:Boolean
)
data class GetPendingRequestsResponse (
    var hasError:Boolean,
    var requests:List<Request>
)

data class RequestDetailsResponse (
    val isRequestFound:Boolean,
    var name:String,
    var age:Int,
    var gender:String,
    var bloodGroup:String,
    var isAccident:Boolean,
    var driverName:String,
    var driverMobile:String,
    val requestedBy:String,
    var location:LocationClass

        )
data class UpdateRequestDetails (
    var requestId:String,
    var requestStatus:String,
)
data class UpdateRequestDetailsPayload (
  var requestDetails:UpdateRequestDetails
        )

data class UpdateRequestDetailsResponse (
    var hasError:Boolean,
        )

data class UpdateJourneyDetailsPayload (
    var journeyDetails:UpdateJourneyDetails
)

data class UpdateJourneyDetailsResponse (
    var hasError:Boolean,
)


data class UpdateJourneyDetails(
    var requestId:String,
    var ambulanceId:String,
    var journeyStatus:String,
)
data class LocationUpdate (
    var journeyStatus:String,
    var currentLocation:List<LocationClass>,
    var location:LocationClass,
    var _id:String
    )


data class GetLocationUpdateResponse (
    var hasError:Boolean,
    var locationUpdate:LocationUpdate
)
data class UpdateLocationDetails (
    var requestId:String,
    var location:LocationClass
        )
data class UpdateLocationPayload (
   var locationUpdateDetails:UpdateLocationDetails
        )
data class UpdateLocationResponse(
    var isUpdated:Boolean,
)

data class GetRequestDetailsByAmbulanceResponse (
    val hasError: Boolean,
    val request:RequestDetailsResponse
        )