package com.example.rawan.radio

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by rawan on 22/11/18.
 */
class AboutFragment:Fragment(){
    companion object {
        fun newInstance():AboutFragment {
            return AboutFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }
}