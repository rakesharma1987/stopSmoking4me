package com.example.stopsmoking4me.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stopsmoking4me.fragments.QuotesFragment
import com.example.stopsmoking4me.fragments.StatesAndChartsFragment
import com.example.stopsmoking4me.fragments.TakeMyPermissionFragment

private const val NUM_TABS = 3

class ViewPagerAdapter(private val fragmentManager: FragmentManager, private val lifecycle: Lifecycle):
FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return TakeMyPermissionFragment()
            1 -> return QuotesFragment()
        }
        return StatesAndChartsFragment()
    }
}