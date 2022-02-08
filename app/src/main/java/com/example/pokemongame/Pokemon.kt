package com.example.pokemongame

import android.location.Location

class Pokemon {
    var name:String? = null
    var des:String? = null
    var image:Int? = null
    var power:Double? = null
    var location:Location? = null
    var cathced:Boolean? = false
    constructor(image:Int,name:String,des:String, power:Double,lat:Double,log:Double) {
        this.name = name
        this.des = des
        this.image = image
        this.power = power
        location!!.latitude = lat
        location!!.longitude = log
        this.cathced = false
    }
}