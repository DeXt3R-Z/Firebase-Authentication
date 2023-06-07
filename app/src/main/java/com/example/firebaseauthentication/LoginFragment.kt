package com.example.firebaseauthentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var et_email: EditText
    private lateinit var et_Password: EditText
    private lateinit var btnLogin : View
    private lateinit var tvRegisterNow: TextView
    private lateinit var btnGoogleSignIn: View
    private var REQ_CODE = 1
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v =  inflater.inflate(R.layout.fragment_login, container, false)
        firebaseAuth = Firebase.auth
        v.apply {
            et_email = findViewById(R.id.et_email)
            et_Password = findViewById(R.id.et_Password)
            btnLogin = findViewById(R.id.btnLogin)
            tvRegisterNow = findViewById(R.id.tv_RegisterNow)
            btnGoogleSignIn = findViewById(R.id.view_Google)

            tvRegisterNow.setOnClickListener {
                parentFragmentManager.beginTransaction().apply {
                    addToBackStack(null)
                    replace(R.id.FragmentHolder,RegistrationFragment())
                    commit()
                }
            }

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context,gso)

            btnGoogleSignIn.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent,REQ_CODE)
            }


            btnLogin.setOnClickListener {
                if(et_email.text.toString().isNotEmpty() && et_Password.text.toString().isNotEmpty())
                {
                    CoroutineScope(Dispatchers.IO).launch {
                        firebaseAuth.signInWithEmailAndPassword(et_email.text.toString(),et_Password.text.toString()).addOnCompleteListener {
                            if(it.isSuccessful)
                            {
                                parentFragmentManager.beginTransaction().apply {
                                    replace(R.id.FragmentHolder,HomeScreen())
                                    commit()
                                }
                                Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }


        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQ_CODE)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                firebaseGoogleAuth(account)
            }catch (e: Exception)
            {
                Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun firebaseGoogleAuth(account: GoogleSignInAccount){
        val email = account.email
        firebaseAuth.fetchSignInMethodsForEmail(email!!).addOnCompleteListener {
            if(it.result?.signInMethods?.size  == 0)
            {
                Toast.makeText(context,"Account does not exist, Register to continue",Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder,RegistrationFragment())
                    commit()
                }
            }
            else
            {
                val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
                firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.FragmentHolder,HomeScreen())
                            commit()
                        }
                        Toast.makeText(context,"Login Successful",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(context,"Login Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}