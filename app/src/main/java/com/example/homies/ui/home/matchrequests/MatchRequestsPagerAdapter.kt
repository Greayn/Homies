package com.example.homies.ui.home.matchrequests

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.homies.ui.home.matchrequests.received.MatchRequestsReceivedPageFragment
import com.example.homies.ui.home.matchrequests.sent.MatchRequestsSentPageFragment
import com.example.homies.ui.home.matchrequests.matched.MatchRequestsMatchedPageFragment

class MatchRequestsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MatchRequestsReceivedPageFragment()
            1 -> MatchRequestsSentPageFragment()
            2 -> MatchRequestsMatchedPageFragment()
            else -> error("Unknown position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Alınan"
            1 -> "Gönderilen"
            2 -> "Eşleşilen"
            else -> error("Unknown position")
        }
    }

}