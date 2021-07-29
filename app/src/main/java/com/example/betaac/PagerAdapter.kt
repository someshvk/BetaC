package com.example.betaac

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.Fragment as Fragment

class PagerAdapter(@NonNull fm: FragmentManager, behavior: Int): FragmentPagerAdapter(fm, behavior) {
    var tabcount: Int = behavior

    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {
        if(position == 0) return  FragmentChat()

        else if(position == 1) return  FragmentContact()

        return FragmentChat()
    }

}
