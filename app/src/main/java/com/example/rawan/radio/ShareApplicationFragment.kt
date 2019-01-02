package com.example.rawan.radio

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by rawan on 22/11/18.
 */
class ShareApplicationFragment :Fragment() {
    companion object {
        fun newInstance():ShareApplicationFragment {
            return ShareApplicationFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share_app, container, false)
    }
}