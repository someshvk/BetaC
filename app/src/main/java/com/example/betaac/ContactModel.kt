package com.example.betaac

class ContactModel(private var name: String = "", private var number: String = "") {

    fun getName(): String{
        return name
    }
    fun getNumber(): String{
        return number
    }
    fun setName(n: String){
        name = n
    }
    fun setNumber(num: String){
        number = num
    }

}
