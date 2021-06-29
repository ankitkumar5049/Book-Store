package com.example.bookstore.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bookstore.R


class profileFragment : Fragment() {
    lateinit var btnUpdateProfile : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = return inflater.inflate(R.layout.fragment_profile, container, false)

            Toast.makeText(activity as Context,"Sorry for the issue. We are working on it. Thank You",Toast.LENGTH_SHORT).show()


        return view
    }


}