package com.example.betaac.chat

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.betaac.MainActivity
import com.example.betaac.PagerAdapter
import com.example.betaac.R
import com.example.betaac.user.ProfileActivity
import com.example.betaac.user.SetProfileActivity
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private var mchat : TabItem? = null
    private var mcontact : TabItem? = null
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var mtoolbar : Toolbar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        tabLayout = findViewById(R.id.include)
        mchat = findViewById(R.id.chat)
        mcontact = findViewById(R.id.contacts)
        viewPager = findViewById(R.id.fragmentContainer)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        mtoolbar = findViewById(R.id.toobar)
        setSupportActionBar(mtoolbar)

        val drawable: Drawable? = ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_more_vert_24)
        mtoolbar.overflowIcon = drawable

        pagerAdapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = pagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position

                if(tab.position == 0 || tab.position == 1){
                    pagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        viewPager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(tabLayout){})
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> startActivity(Intent(this@ChatActivity, ProfileActivity::class.java))

            R.id.logout -> {
                firebaseAuth.signOut()
                checkUser()
                Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser==null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            val phone= firebaseUser.phoneNumber
        }
    }
    override fun onResume() {
        super.onResume()
        val currentId: String = FirebaseAuth.getInstance().uid.toString()
        firebaseDatabase.reference.child("presence").child(currentId).setValue("Online")
    }
}