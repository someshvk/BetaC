package com.example.betaac.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.betaac.MainActivity
import com.example.betaac.R
import com.example.betaac.chat.ChatActivity
import com.example.betaac.databinding.ActivityOtpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*

class OtpActivity : AppCompatActivity() {
    private  lateinit var  mchangeNumber: TextView
    private  lateinit var mgetOtp: EditText
    private  lateinit var mverifyOtp: Button
    private lateinit var enteredOtp: String

    private  lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var mprogressbarOtp: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        mchangeNumber = findViewById(R.id.changeNumber)
        mgetOtp = findViewById(R.id.veriftnCode)
        mverifyOtp = findViewById(R.id.verifyBtn)
        mprogressbarOtp = findViewById(R.id.progressBarOtp)

        firebaseAuth= FirebaseAuth.getInstance()

        mchangeNumber.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@OtpActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        mverifyOtp.setOnClickListener(View.OnClickListener {
            enteredOtp = mgetOtp.text.toString()
            if(enteredOtp.isEmpty()){
                Toast.makeText(applicationContext, "Enter your otp first.", Toast.LENGTH_SHORT).show()
            }
            else{
                mprogressbarOtp.visibility = View.VISIBLE
                val codeReceived: String? = intent.getStringExtra("otp")
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(codeReceived.toString(), enteredOtp)
                signInWithPhoneAuthCredential(credential)
            }
        })
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(OnCompleteListener<AuthResult>() {task->
            if (task.isSuccessful){
                mprogressbarOtp.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, "Login Successful.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@OtpActivity, ChatActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                if(task.exception is FirebaseAuthInvalidCredentialsException){
                    mprogressbarOtp.visibility = View.INVISIBLE
                    Toast.makeText(applicationContext, "Login Failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}