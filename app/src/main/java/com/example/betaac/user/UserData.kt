package com.example.betaac.user

class UserData(
    private var userName: String="",
    private var userID: String="")
{
    fun getUsername(): String{
        return userName
    }
    fun setUsername(name: String){
        userName = name
    }

    fun getUserId(): String{
        return userID
    }

    fun setUserId(uid: String){
        userID = uid
    }
}

