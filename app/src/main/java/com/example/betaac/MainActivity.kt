package com.example.betaac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.betaac.chat.ChatActivity
import com.example.betaac.user.OtpActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mgetPhoneNumber: EditText
    private lateinit var msendOtp: Button
    private lateinit var mcountryCodePicker: CountryCodePicker
    private lateinit var countryCode: String
    private lateinit var phoneNumber: String
    private lateinit var codeSent: String

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mprogressBarMain: ProgressBar
    private lateinit var mcallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mgetPhoneNumber= findViewById(R.id.phoneNumber)
        msendOtp = findViewById(R.id.otpBtn)
        mcountryCodePicker = findViewById(R.id.countryCodePicker)
        mprogressBarMain = findViewById(R.id.progressBarMain)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        countryCode = mcountryCodePicker.selectedCountryCodeWithPlus

        mcountryCodePicker.setOnCountryChangeListener(CountryCodePicker.OnCountryChangeListener {
                countryCode = mcountryCodePicker.selectedCountryCodeWithPlus
        })
        msendOtp.setOnClickListener(View.OnClickListener {
            var number: String = mgetPhoneNumber.text.toString()
            if(number.isEmpty()){
                Toast.makeText(applicationContext, "Please enter your number.", Toast.LENGTH_SHORT).show()
            }
            else if(number.length < 10){
                Toast.makeText(applicationContext, "Please enter correct number.", Toast.LENGTH_SHORT).show()
            }
            else{
                mprogressBarMain.visibility = View.VISIBLE
                phoneNumber = countryCode + number

                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mcallbacks!!)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        })

        mcallbacks= object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                Toast.makeText(applicationContext, "OTP sent.", Toast.LENGTH_SHORT).show()
                mprogressBarMain.visibility = View.INVISIBLE
                codeSent = verificationId

                val intent = Intent(this@MainActivity, OtpActivity::class.java)
                intent.putExtra("otp", codeSent)
                startActivity(intent)
                finish()

            }
        }
    }
    override fun onStart(){
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId: String = FirebaseAuth.getInstance().uid.toString()
        firebaseDatabase.reference.child("presence").child(currentId).setValue("Online")
    }
    override fun onPause() {
        super.onPause()
        val currentId: String = FirebaseAuth.getInstance().uid.toString()
        firebaseDatabase.reference.child("presence").child(currentId).setValue("Offline")
    }
}