package com.example.firebaseauthentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeScreen : Fragment() {

    private lateinit var btnLogout: AppCompatButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val firebaseAuth = Firebase.auth
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_home_screen, container, false)
        v.apply {
            btnLogout = findViewById(R.id.btn_logout)
            btnLogout.setOnClickListener {
                firebaseAuth.signOut()
            }
        }
        return v
    }

}