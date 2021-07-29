package com.example.betaac.chat


class Messages(private var message: String = "",
               private var senderId: String = "",
               private var currentTime: String = "",
               private var timestamp: String = "",
               private var imgUrl: String = "") {

    fun getMessage(): String{
        return message
    }
    fun getSenderId(): String{
        return senderId
    }
    fun getCurrentTime(): String{
        return currentTime
    }
    fun getTimestamp(): String{
        return timestamp
    }
    fun getImageUrl(): String{
        return imgUrl
    }

    fun setMessage(msg: String){
        message = msg
    }
    fun setSenderId(Sid: String){
        senderId = Sid
    }
    fun setCurrentTime(ct: String){
        currentTime = ct
    }
    fun setTimestamp(ts: String){
        timestamp = ts
    }

    fun setImageUrl(url: String) {
        imgUrl = url
    }
}