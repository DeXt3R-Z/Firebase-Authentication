package com.example.firebaseauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var auth: FirebaseAuth = Firebase.auth


        if(auth.currentUser==null)
        {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder,LoginFragment())
                commit()
            }
        }
        else
        {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder,HomeScreen())
                commit()
            }
        }

    }
}