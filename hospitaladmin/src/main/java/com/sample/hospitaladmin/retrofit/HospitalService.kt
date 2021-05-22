package com.sample.hospitaladmin.retrofit

import com.sample.hospitaladmin.home.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HospitalService {

    @GET("hospital/{hospitalId}")
    suspend fun check(@Path("hospitalId") hospitalId: String) : HospitalCheckResponse
    @POST("hospital")
    suspend fun signUpHospital(@Body payload: HospitalSignUpPayload): HospitalSignupResponse
    @POST("hospital/login")
    suspend fun hospitalLogin(@Body payload:HospitalLoginPayload):LogInResponse
    @POST("ambulance")
    suspend fun signUpAmbulance(@Body payload:AmbulanceSignupPayload):AmbulanceSignupResponse
    @GET("ambulance/{ambulanceId}")
    suspend fun checkAmbulanceIfExists(@Path("ambulanceId") ambulanceId:String) : AmbulanceCheckResponse
    @POST("ambulance/login")
    suspend fun ambulanceLogin(@Body payload:AmbulanceLoginPayload) :LogInResponse
    @GET("request-and-journey/hospital/{hospitalId}")
    suspend fun getPendingRequestsByHospital(@Path("hospitalId") hospitalId:String) : GetPendingRequestsResponse
    @GET("request-and-journey/{requestId}")
    suspend fun getRequestDetails(@Path("requestId") requestId:String) : RequestDetailsResponse
}