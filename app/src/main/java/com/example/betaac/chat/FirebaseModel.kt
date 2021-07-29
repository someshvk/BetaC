package com.example.betaac.chat

class FirebaseModel(private var name: String= "", private var image: String="", private var uid: String= "") {

    fun getName(): String{
        return name
    }
    fun setName(name1: String){
        name = name1
    }
    fun getImg(): String{
        return image
    }
    fun setImg(image1: String){
        image = image1
    }
    fun getUid(): String{
        return uid
    }
    fun setUid(uid1: String){
        uid = uid1
    }
}