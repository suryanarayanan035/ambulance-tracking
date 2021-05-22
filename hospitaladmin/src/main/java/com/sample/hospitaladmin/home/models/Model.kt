package com.sample.hospitaladmin.home.models

import android.graphics.Point
import android.os.Parcelable
import java.io.Serializable

class Model {

}
data class Address (
    public var street:String,
    public var district:String,
    public var pincode:String,
    )
data class LocationClass (
    public var type:String,
    public  var coordinates:List<Double>
        )
 class Hospital :Serializable {
     public lateinit var mobile: String
     public lateinit var name: String
     public lateinit var type: String
     public  lateinit var password: String
     public lateinit var address: Address
     public lateinit var location: LocationClass

 }